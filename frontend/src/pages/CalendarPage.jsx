import { useEffect, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import { useToast } from "../components/useToast.jsx";

/**
 * ============================================================
 * CalendarPage Component
 * ============================================================
 *
 * PURPOSE:
 * - Displays a weekly calendar view of the user's schedule
 * - Allows semester switching
 * - Shows course details on click
 * - Allows course removal directly from calendar
 * - Displays total credits dynamically
 *
 * DATA FLOW:
 * Backend → /api/calendar → transform → FullCalendar events
 *
 * KEY CONCEPT:
 * - Backend gives "blocks"
 * - We transform them into FullCalendar-compatible events
 *
 * ============================================================
 */

function CalendarPage() {

    /* =========================================================
       TOAST SYSTEM (UI notifications)
    ========================================================= */
    const toast = useToast();

    /* =========================================================
       STATE MANAGEMENT
    ========================================================= */

    /**
     * Calendar events formatted for FullCalendar
     * Each event represents ONE meeting block (not full course)
     */
    const [events, setEvents] = useState([]);

    /**
     * Currently selected course (clicked from calendar)
     * Used to display popup details
     */
    const [selectedCourse, setSelectedCourse] = useState(null);

    /**
     * All available semesters from backend
     */
    const [semesters, setSemesters] = useState([]);

    /**
     * Currently selected semester
     * Drives filtering of events
     */
    const [selectedSemester, setSelectedSemester] = useState("");

    /* =========================================================
       DERIVED STATE
    ========================================================= */

    /**
     * Calculate total credits dynamically
     *
     * WHY Map?
     * - Avoid duplicate courses (same course may have multiple meeting blocks)
     *
     * Steps:
     * 1. Map events → unique courses by courseCode
     * 2. Extract values
     * 3. Sum credits
     */
    const totalCredits = [...new Map(
        events.map(e => [
            e.extendedProps.course?.courseCode,
            e.extendedProps.course
        ])
    ).values()]
    .reduce((sum, course) =>
        sum + (Number(course?.credits) || 0),
        0
    );

    /* =========================================================
       UTILITY FUNCTIONS
    ========================================================= */

    /**
     * Converts day letter → FullCalendar numeric format
     *
     * FullCalendar uses:
     * 0 = Sunday, 1 = Monday, ...
     *
     * Your system:
     * M T W R F
     */
    const dayToNumber = (day) => {
        const map = {
            "M": 1,
            "T": 2,
            "W": 3,
            "R": 4,
            "F": 5
        };
        return map[day];
    };

    /**
     * Converts time → human readable format
     *
     * Input:
     * - ["13", "30"] OR "13:30"
     *
     * Output:
     * - "1:30 PM"
     */
    const formatTime = (timeArray) => {
        if (!timeArray) return "TBA";

        let hour, minute;

        if (Array.isArray(timeArray)) {
            hour = Number(timeArray[0]);
            minute = Number(timeArray[1] ?? 0);
        } else if (typeof timeArray === "string") {
            const [h, m] = timeArray.split(":");
            hour = Number(h);
            minute = Number(m ?? 0);
        }

        if (Number.isNaN(hour) || Number.isNaN(minute)) return "TBA";

        const minuteLabel = minute.toString().padStart(2, "0");
        const period = hour >= 12 ? "PM" : "AM";
        const hour12 = hour % 12 || 12;

        return `${hour12}:${minuteLabel} ${period}`;
    };

    /**
     * Converts time → FullCalendar-compatible format
     *
     * Output:
     * "HH:mm:ss"
     */
    const toCalendarTime = (timeArray) => {
        if (!timeArray) return null;

        let hour, minute;

        if (Array.isArray(timeArray)) {
            hour = Number(timeArray[0]);
            minute = Number(timeArray[1] ?? 0);
        } else if (typeof timeArray === "string") {
            const [h, m] = timeArray.split(":");
            hour = Number(h);
            minute = Number(m ?? 0);
        }

        if (Number.isNaN(hour) || Number.isNaN(minute)) return null;

        return `${hour.toString().padStart(2, "0")}:${minute.toString().padStart(2, "0")}:00`;
    };

    /* =========================================================
       DATA FETCHING + TRANSFORMATION
    ========================================================= */

    /**
     * Fetch calendar data and transform into events
     *
     * Steps:
     * 1. Fetch raw blocks from backend
     * 2. Extract semesters
     * 3. Choose active semester
     * 4. Convert blocks → calendar events
     */
    const fetchCalendar = async (semester) => {

        const response = await fetch("/api/calendar");
        const data = await response.json();

        /* -----------------------------
           STEP 1: Extract semesters
        ----------------------------- */
        const semesterSet = new Set();

        for (const block of data.blocks) {
            if (block.course?.semester) {
                semesterSet.add(block.course.semester);
            }
        }

        const semesterList = Array.from(semesterSet);
        setSemesters(semesterList);

        /* -----------------------------
           STEP 2: Determine active semester
        ----------------------------- */
        const activeSemester = semester || semesterList[0] || "";

        // Default to first semester on initial load
        if (!semester && semesterList.length > 0) {
            setSelectedSemester(semesterList[0]);
        }

        /* -----------------------------
           STEP 3: Transform data
        ----------------------------- */
        const mapped = [];

        for (const block of data.blocks) {
            const course = block.course;

            // Skip invalid entries
            if (!course || !course.dayTimeMap) continue;

            // Filter by semester
            if (course.semester !== activeSemester) continue;

            /**
             * Convert each meeting into a separate calendar event
             */
            for (const [day, range] of Object.entries(course.dayTimeMap)) {

                const dayNum = dayToNumber(day);

                const start = Array.isArray(range)
                    ? toCalendarTime(range[0])
                    : null;

                const end = Array.isArray(range)
                    ? toCalendarTime(range[1])
                    : null;

                if (dayNum === undefined || !start || !end) continue;

                mapped.push({
                    title: course.courseName,
                    daysOfWeek: [dayNum],
                    startTime: start,
                    endTime: end,

                    /**
                     * Store extra data for click handling
                     */
                    extendedProps: {
                        course,
                        day,
                        startTime: range[0],
                        endTime: range[1]
                    }
                });
            }
        }

        setEvents(mapped);
    };

    /* =========================================================
       EFFECTS
    ========================================================= */

    /**
     * Re-fetch calendar when semester changes
     */
    useEffect(() => {
        fetchCalendar(selectedSemester);
    }, [selectedSemester]);

    /* =========================================================
       EVENT HANDLERS
    ========================================================= */

    /**
     * When a calendar event is clicked:
     * - Extract course info
     * - Open details panel
     */
    const handleEventClick = (info) => {
        const { course, day, startTime, endTime } =
            info.event.extendedProps;

        setSelectedCourse({
            ...course,
            day,
            startTime,
            endTime
        });
    };

    /**
     * Remove selected course from schedule
     */
    const removeCourse = async () => {

        const response = await fetch(
            `/api/mySchedule/remove/${selectedCourse.courseCode}/${selectedCourse.semester}`,
            { method: "DELETE" }
        );

        if (response.ok) {
            toast({
                message: `${selectedCourse.courseCode} removed from schedule.`,
                type: "info"
            });

            setSelectedCourse(null);

            // Refresh calendar
            await fetchCalendar(selectedSemester);

        } else {
            toast({
                message: "Failed to remove course.",
                type: "error"
            });
        }
    };

    /**
     * Opens schedule PDF in new tab
     */
    const downloadPDF = () => {
        window.open("/api/mySchedule/pdf", "_blank");
    };

    /* =========================================================
       RENDER
    ========================================================= */

    return (
        <div>

            {/* ================= HEADER ================= */}
            <h1>Weekly Schedule</h1>

            {/* ================= CONTROLS ================= */}
            <div style={{
                marginBottom: "16px",
                display: "flex",
                alignItems: "center",
                gap: "20px"
            }}>

                {/* Semester Selector */}
                <div>
                    <label style={{ fontWeight: "bold" }}>
                        Semester:
                    </label>

                    <select
                        value={selectedSemester}
                        onChange={(e) =>
                            setSelectedSemester(e.target.value)
                        }
                    >
                        {semesters.map((sem) => (
                            <option key={sem} value={sem}>
                                {sem}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Total Credits */}
                <div style={{ fontWeight: "bold" }}>
                    Total Credits:
                    <span style={{ color: "#2563eb" }}>
                        {totalCredits}
                    </span>
                </div>
            </div>

            {/* ================= COURSE POPUP ================= */}
            {selectedCourse && (
                <div className="card">

                    <h3>{selectedCourse.courseName}</h3>

                    <p><b>Code:</b> {selectedCourse.courseCode}</p>
                    <p><b>Professor:</b> {selectedCourse.professor}</p>
                    <p><b>Time:</b>
                        {formatTime(selectedCourse.startTime)} -
                        {formatTime(selectedCourse.endTime)}
                    </p>

                    <button onClick={removeCourse}>
                        Remove Course
                    </button>

                    <button onClick={() =>
                        setSelectedCourse(null)
                    }>
                        Close
                    </button>
                </div>
            )}

            {/* ================= PDF BUTTON ================= */}
            <button onClick={downloadPDF}>
                Download Schedule PDF
            </button>

            {/* ================= CALENDAR ================= */}
            <div className="card">
                <FullCalendar
                    plugins={[timeGridPlugin, interactionPlugin]}
                    initialView="timeGridWeek"
                    events={events}
                    eventClick={handleEventClick}
                />
            </div>
        </div>
    );
}

export default CalendarPage;