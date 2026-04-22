import { useEffect, useState } from "react";
import { useToast } from "../components/useToast.jsx";

/**
 * SearchPage Component
 * ------------------------------------------------------------------
 * PURPOSE:
 * - Allows users to search for courses
 * - Apply filters (department, professor, etc.)
 * - View results in a table
 * - Add/remove courses from schedule
 * - Handle UI state like expanded descriptions
 *
 * IMPORTANT ARCHITECTURE NOTE:
 * - All state is lifted to App.jsx
 * - This component RECEIVES state + setters via props
 * - This component is responsible for:
 *     - triggering API calls
 *     - formatting/displaying data
 *     - updating parent state
 */
function SearchPage({
    query, setQuery,
    results, setResults,
    filters, setFilters,
    expandedDescriptions, setExpandedDescriptions,
    selectedSemester, setSelectedSemester,
    schedule, fetchSchedule,
    darkMode
}) {

    /* =========================================================
       TOAST SYSTEM
       - Custom hook used to show notifications instead of alert()
    ========================================================= */
    const toast = useToast();

    /* =========================================================
       RATE MY PROFESSOR DATA
       - Fetched once on mount from /api/professors
       - Stored as a map: "Lastname, Firstname" → { avgRating, avgDifficulty }
    ========================================================= */
    const [professorMap, setProfessorMap] = useState({});

    /* =========================================================
       CONSTANTS
    ========================================================= */

    /**
     * Maximum length before description is truncated
     */
    const DESCRIPTION_PREVIEW_LENGTH = 100;

    /* =========================================================
       DERIVED DATA (computed each render)
    ========================================================= */

    /**
     * Filters results by selected semester (client-side filter)
     * If no semester selected → show all results
     */
    const filteredResults = selectedSemester
        ? results.filter(c => c.semester === selectedSemester)
        : results;

    /**
     * Builds list of unique semesters from results
     * - removes duplicates using Set
     * - removes null/undefined
     * - sorts alphabetically
     */
    const availableSemesters =
        [...new Set(results.map(c => c.semester).filter(Boolean))].sort();

    /* =========================================================
       UTILITY FUNCTIONS
    ========================================================= */

    /**
     * Sort courses alphabetically by courseCode
     * Ensures consistent UI ordering
     */
    const sortByCourseCode = (courses) => {
        return [...courses].sort((a, b) =>
            a.courseCode.localeCompare(b.courseCode)
        );
    };

    /**
     * Converts backend time format → human readable time
     *
     * Supports:
     * - ["13", "30"]
     * - "13:30"
     *
     * Output:
     * - "1:30 PM"
     */
    const formatTime = (time) => {
        if (!time) return "TBA";

        let hour, minute;

        if (Array.isArray(time)) {
            hour = Number(time[0]);
            minute = Number(time[1] ?? 0);
        } else if (typeof time === "string") {
            const [h, m] = time.split(":");
            hour = Number(h);
            minute = Number(m ?? 0);
        }

        if (Number.isNaN(hour) || Number.isNaN(minute)) return "TBA";

        const period = hour >= 12 ? "PM" : "AM";
        const hour12 = hour % 12 || 12;
        const min = minute.toString().padStart(2, "0");

        return `${hour12}:${min} ${period}`;
    };

    /**
     * Extracts meeting entries from course.dayTimeMap
     *
     * Example:
     * { M: ["10:00", "11:00"], W: ["10:00", "11:00"] }
     * → [ ["M", [...]], ["W", [...]] ]
     */
    const getMeetings = (course) =>
        Object.entries(course.dayTimeMap ?? {});

    /**
     * Converts meeting days into readable string
     * Example: ["M", "W"] → "M, W"
     */
    const formatDays = (course) => {
        const days = getMeetings(course).map(([day]) => day);
        return days.length > 0 ? days.join(", ") : "TBA";
    };

    /**
     * Formats meeting time ranges into readable string
     *
     * Example:
     * ["10:00", "11:00"] → "10:00 AM - 11:00 AM"
     *
     * Also removes duplicate time ranges
     */
    const formatMeetingTimes = (course) => {
        const ranges = getMeetings(course)
            .map(([, range]) => {
                if (!Array.isArray(range)) return null;
                return `${formatTime(range[0])} - ${formatTime(range[1])}`;
            })
            .filter(Boolean);

        const uniqueRanges = [...new Set(ranges)];
        return uniqueRanges.length > 0
            ? uniqueRanges.join("; ")
            : "TBA";
    };

    /**
     * Safely returns trimmed description
     */
    const getDescription = (course) =>
        (course.description ?? "").trim();

    /* =========================================================
       UI STATE HANDLERS
    ========================================================= */

    /**
     * Toggles description expansion for a specific course
     *
     * Uses object map:
     * { "COMP101-0": true }
     */
    const toggleDescription = (courseKey) => {
        setExpandedDescriptions({
            ...expandedDescriptions,
            [courseKey]: !expandedDescriptions[courseKey]
        });
    };

    /* =========================================================
       API CALLS
    ========================================================= */

    /**
     * Clears all filters and reloads base dataset
     */
    const clearFilter = async () => {
        await fetchSchedule();

        const response = await fetch(`/api/noFilters`);
        const data = await response.json();

        if (response.ok) {
            setFilters({
                department: "",
                professor: "",
                credits: "",
                days: [],
                startTime: "",
                endTime: ""
            });

            setResults(sortByCourseCode(Array.from(data ?? [])));
        } else {
            setResults([]);
        }
    };

    /**
     * Applies filters by sending query params to backend
     */
    const runFilter = async () => {
        const hasFilters =
            filters.department ||
            filters.professor ||
            filters.credits ||
            filters.days.length > 0 ||
            filters.startTime ||
            filters.endTime;

        if (!hasFilters) {
            await clearFilter();
            return;
        }

        const params = new URLSearchParams();

        if (filters.department) params.append("department", filters.department);
        if (filters.professor) params.append("professor", filters.professor);
        if (filters.credits) params.append("credits", filters.credits);
        if (filters.days.length > 0) params.append("days", filters.days.join(","));
        if (filters.startTime) params.append("startTime", filters.startTime + ":00");
        if (filters.endTime) params.append("endTime", filters.endTime + ":00");

        const response = await fetch(
            `/api/filterResults/${query}/filter?${params}`,
            { method: "POST" }
        );

        if (response.ok) {
            const data = await response.json();
            setExpandedDescriptions({});
            setResults(sortByCourseCode(Array.from(data)));
        }
    };

    /**
     * Executes search query against backend
     */
    const runSearch = async () => {
        await fetchSchedule();

        const response = await fetch(`/api/searchResults/${query}`, {
            method: "POST"
        });

        const data = await response.json();

        if (response.ok) {
            setResults(sortByCourseCode(Array.from(data.results ?? [])));
        } else {
            setResults([]);
        }
    };

    /* =========================================================
       EFFECTS (REACTIVE LOGIC)
    ========================================================= */

    /**
     * Fetches RateMyProfessor data once on mount.
     * Builds a lookup map keyed by "Lastname, Firstname" to match
     * the course.professor field format from the backend.
     */
    useEffect(() => {
        const fetchProfessors = async () => {
            try {
                const res = await fetch("/api/professors");
                if (!res.ok) return;
                const edges = await res.json(); // Array of { cursor, node: { firstName, lastName, avgRating, avgDifficulty, ... } }
                const map = {};
                for (const edge of edges) {
                    const { firstName, lastName, avgRating, avgDifficulty, numRatings } = edge.node ?? {};
                    if (!lastName) continue;
                    // Key: "Lastname, Firstname" — matches backend professor field
                    const key = `${lastName}, ${firstName}`.toLowerCase();
                    map[key] = { avgRating, avgDifficulty, numRatings };
                }
                setProfessorMap(map);
            } catch (e) {
                // RMP data is non-critical; silently fail
            }
        };
        fetchProfessors();
    }, []);

    /**
     * Looks up RMP data for a professor name string.
     * Tries full "Lastname, Firstname" match, then last-name-only fallback.
     */
    const getRmpData = (professorName) => {
        if (!professorName) return null;
        const key = professorName.toLowerCase();
        if (professorMap[key]) return professorMap[key];
        // Fallback: match by last name only (first word before comma)
        const lastName = key.split(",")[0].trim();
        const fallback = Object.keys(professorMap).find(k => k.startsWith(lastName + ","));
        return fallback ? professorMap[fallback] : null;
    };

    /**
     * Runs search when query changes (debounced)
     */
    useEffect(() => {
        const debounceTimer = setTimeout(async () => {
            await runSearch();

            const hasFilters =
                filters.department ||
                filters.professor ||
                filters.credits ||
                filters.days.length > 0 ||
                filters.startTime ||
                filters.endTime;

            if (hasFilters) {
                await runFilter();
            }
        }, 300);

        return () => clearTimeout(debounceTimer);
    }, [query]);

    /**
     * Runs filter when filters change (debounced)
     */
    useEffect(() => {
        const debounceTimer = setTimeout(() => {
            runFilter();
        }, 300);

        return () => clearTimeout(debounceTimer);
    }, [filters]);

    /* =========================================================
       COURSE ACTIONS
    ========================================================= */

    /**
     * Adds a course to schedule
     */
    const addCourse = async (courseCode, semester) => {
        const response = await fetch(
            `/api/mySchedule/add/${courseCode}/${semester}`,
            { method: "POST" }
        );

        if (response.ok) {
            toast({ message: `${courseCode} added to schedule!`, type: "success" });
            await fetchSchedule();
        } else if (response.status === 500) {
            toast({ message: "Conflict with existing course.", type: "error" });
        } else {
            toast({ message: "Course not found.", type: "error" });
        }
    };

    /**
     * Removes a course from schedule
     */
    const removeCourse = async (courseCode, semester) => {
        const response = await fetch(
            `/api/mySchedule/remove/${courseCode}/${semester}`,
            { method: "DELETE" }
        );

        if (response.ok) {
            toast({ message: `${courseCode} removed.`, type: "info" });
            await fetchSchedule();
        } else {
            toast({ message: "Failed to remove course.", type: "error" });
        }
    };

    /**
     * Random course picker with conflict avoidance
     */
    const feelingLucky = async () => {
        const response = await fetch("/api/noFilters");
        if (!response.ok) return;

        const data = await response.json();

        const pool = selectedSemester
            ? data.filter(c => c.semester === selectedSemester)
            : data;

        const notAdded = pool.filter(
            c => !schedule.some(s =>
                s.courseCode === c.courseCode &&
                s.semester === c.semester
            )
        );

        if (!notAdded.length) {
            alert("No available courses!");
            return;
        }

        const shuffled = [...notAdded].sort(() => Math.random() - 0.5);

        for (const candidate of shuffled) {
            const res = await fetch(
                `/api/mySchedule/add/${candidate.courseCode}/${candidate.semester}`,
                { method: "POST" }
            );

            if (res.ok) {
                await fetchSchedule();
                alert(`Added ${candidate.courseCode}`);
                return;
            }
        }

        alert("All options conflicted.");
    };

    const totalCredits = schedule.reduce((sum, c) => sum + (Number(c.credits) || 0), 0);

    /* =========================================================
       RENDER (UI)
    ========================================================= */

    return (
        <div>
            <h1>Search</h1>

            {/* ── MY SCHEDULE ── */}
            <div className="card" style={{ marginBottom: "20px" }}>
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "10px" }}>
                    <h2 style={{ margin: 0 }}>My Schedule</h2>
                    {schedule.length > 0 && (
                        <span style={{ backgroundColor: "#1d54a1", color: "white", padding: "4px 12px", borderRadius: "12px", fontSize: "13px", fontWeight: "bold" }}>
                            {totalCredits} credit{totalCredits !== 1 ? "s" : ""}
                        </span>
                    )}
                </div>

                {schedule.length === 0 ? (
                    <p style={{ color: "#6b7280", fontStyle: "italic", margin: 0 }}>
                        No courses added yet. Use the search below to build your schedule.
                    </p>
                ) : (
                    <table style={{ width: "100%", borderCollapse: "collapse", tableLayout: "fixed" }}>
                        <thead>
                            <tr style={{ backgroundColor: "#1d54a1", color: "white", textAlign: "left" }}>
                                <th style={{ padding: "10px", width: "10%" }}>Code</th>
                                <th style={{ padding: "10px", width: "30%" }}>Name</th>
                                <th style={{ padding: "10px", width: "14%" }}>Professor</th>
                                <th style={{ padding: "10px", width: "8%" }}>Days</th>
                                <th style={{ padding: "10px", width: "14%" }}>Time</th>
                                <th style={{ padding: "10px", width: "8%" }}>Credits</th>
                                <th style={{ padding: "10px", width: "8%" }}>Semester</th>
                                <th style={{ padding: "10px", width: "8%" }}>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {sortByCourseCode(schedule).map((course, index) => {
                                const rowBg = darkMode
                                    ? (index % 2 === 0 ? "#1f2937" : "#111827")
                                    : (index % 2 === 0 ? "#f9fafb" : "white");
                                const rowColor = darkMode ? "#f9fafb" : "#111827";
                                return (
                                    <tr key={`${course.courseCode}-${course.semester}`} style={{ borderBottom: "1px solid #e5e7eb", backgroundColor: rowBg, color: rowColor }}>
                                        <td style={{ padding: "10px" }}><b>{course.courseCode}</b></td>
                                        <td style={{ padding: "10px" }}>{course.courseName}</td>
                                        <td style={{ padding: "10px" }}>{course.professor}</td>
                                        <td style={{ padding: "10px" }}>{formatDays(course)}</td>
                                        <td style={{ padding: "10px" }}>{formatMeetingTimes(course)}</td>
                                        <td style={{ padding: "10px" }}>{course.credits}</td>
                                        <td style={{ padding: "10px" }}>{course.semester}</td>
                                        <td style={{ padding: "10px" }}>
                                            <button
                                                onClick={() => removeCourse(course.courseCode, course.semester)}
                                                style={{ backgroundColor: "#dc2626", color: "white", border: "none", padding: "6px 12px", borderRadius: "4px", cursor: "pointer" }}
                                            >
                                                Remove
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                )}
            </div>

            {/* ── SEARCH + FILTERS ── */}

            <input
                type="text"
                placeholder="Search courses..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
            />

            <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
                {/* <button onClick={runSearch}>Search</button> */}
                {/* <button onClick={runFilter}>Refresh Filters</button> */}

                <button
                    onClick={() => {
                        setFilters({
                            department: "",
                            professor: "",
                            credits: "",
                            days: [],
                            startTime: "",
                            endTime: ""
                        });
                        clearFilter();
                    }}
                >
                    Clear Filters
                </button>

                <button
                    onClick={feelingLucky}
                    style={{
                        backgroundColor: "#7c3aed",
                        color: "white",
                        border: "none",
                        padding: "8px 16px",
                        borderRadius: "4px",
                        cursor: "pointer"
                    }}
                >
                    🎲 I'm Feeling Lucky
                </button>
            </div>

            <div className="card" style={{ marginBottom: "10px" }}>
                <h3 style={{ marginTop: 0 }}>Filters</h3>

                <div style={{ display: "flex", gap: "20px", flexWrap: "wrap" }}>
                    <label>
                        Department:
                        <input
                            type="text"
                            placeholder="e.g. COMP"
                            value={filters.department}
                            onChange={(e) =>
                                setFilters({ ...filters, department: e.target.value })
                            }
                            style={{ marginLeft: "8px" }}
                        />
                    </label>

                    <label>
                        Professor:
                        <input
                            type="text"
                            placeholder="e.g. Wolfe"
                            value={filters.professor}
                            onChange={(e) =>
                                setFilters({ ...filters, professor: e.target.value })
                            }
                            style={{ marginLeft: "8px" }}
                        />
                    </label>

                    <label>
                        Credits:
                        <input
                            type="number"
                            placeholder="e.g. 3"
                            value={filters.credits}
                            onChange={(e) =>
                                setFilters({ ...filters, credits: e.target.value })
                            }
                            style={{ marginLeft: "8px", width: "60px" }}
                        />
                    </label>

                    <label>
                        Days:
                        {["M", "T", "W", "R", "F"].map((day) => (
                            <label key={day} style={{ marginLeft: "8px" }}>
                                <input
                                    type="checkbox"
                                    checked={filters.days.includes(day)}
                                    onChange={(e) => {
                                        const updated = e.target.checked
                                            ? [...filters.days, day]
                                            : filters.days.filter((d) => d !== day);

                                        setFilters({ ...filters, days: updated });
                                    }}
                                />{" "}
                                {day}
                            </label>
                        ))}
                    </label>

                    <label>
                        Start Time:
                        <input
                            type="time"
                            value={filters.startTime}
                            onChange={(e) =>
                                setFilters({ ...filters, startTime: e.target.value })
                            }
                            style={{ marginLeft: "8px", width: "80px" }}
                        />
                    </label>

                    <label>
                        End Time:
                        <input
                            type="time"
                            value={filters.endTime}
                            onChange={(e) =>
                                setFilters({ ...filters, endTime: e.target.value })
                            }
                            style={{ marginLeft: "8px", width: "80px" }}
                        />
                    </label>
                </div>
            </div>

            <div
                style={{
                    marginBottom: "10px",
                    display: "flex",
                    alignItems: "center",
                    gap: "10px"
                }}
            >
                <label htmlFor="semester-select" style={{ fontWeight: "bold" }}>
                    Semester:
                </label>

                <select
                    id="semester-select"
                    value={selectedSemester}
                    onChange={(e) => setSelectedSemester(e.target.value)}
                    style={{
                        padding: "6px 12px",
                        borderRadius: "4px",
                        border: "1px solid #1f2937",
                        fontSize: "14px"
                    }}
                >
                    <option value="">All</option>
                    {availableSemesters.map((sem) => (
                        <option key={sem} value={sem}>
                            {sem}
                        </option>
                    ))}
                </select>
            </div>

            <div className="card">
                <table
                    style={{
                        width: "100%",
                        borderCollapse: "collapse",
                        tableLayout: "fixed"
                    }}
                >
                    <thead>
                        <tr
                            style={{
                                backgroundColor: "#1f2937",
                                color: "white",
                                textAlign: "left"
                            }}
                        >
                            <th style={{ padding: "10px", width: "10%" }}>Code</th>
                            <th style={{ padding: "10px", width: "30%" }}>Name</th>
                            <th style={{ padding: "10px", width: "14%" }}>Professor</th>
                            <th style={{ padding: "10px", width: "8%" }}>Days</th>
                            <th style={{ padding: "10px", width: "14%" }}>Time</th>
                            <th style={{ padding: "10px", width: "8%" }}>Credits</th>
                            <th style={{ padding: "10px", width: "8%" }}>Semester</th>
                            <th style={{ padding: "10px", width: "8%" }}>Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        {filteredResults.map((course, index) => {
                            const courseKey = `${course.courseCode}-${index}`;
                            const description = getDescription(course);
                            const isExpanded = Boolean(expandedDescriptions[courseKey]);
                            const hasLongDescription =
                                description.length > DESCRIPTION_PREVIEW_LENGTH;

                            const rowBg = darkMode
                                ? (index % 2 === 0 ? "#1f2937" : "#111827")
                                : (index % 2 === 0 ? "#f9fafb" : "white");
                            const rowColor = darkMode ? "#f9fafb" : "#111827";

                            return (
                                <tr
                                    key={index}
                                    style={{
                                        borderBottom: "1px solid #e5e7eb",
                                        backgroundColor: rowBg,
                                        color: rowColor,
                                        opacity: course.isOpen ? 1 : 0.6
                                    }}
                                >
                                    <td style={{ padding: "10px" }}>
                                        <b>{course.courseCode}</b>
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        <div>{course.courseName}</div>

                                        <div
                                            style={{
                                                marginTop: "4px",
                                                fontSize: "0.875rem",
                                                color: "inherit"
                                            }}
                                        >
                                            {description
                                                ? (isExpanded || !hasLongDescription
                                                    ? description
                                                    : description.slice(0, DESCRIPTION_PREVIEW_LENGTH) + "…")
                                                : "No description available."}
                                        </div>

                                        {hasLongDescription && (
                                            <button
                                                type="button"
                                                onClick={() =>
                                                    toggleDescription(courseKey)
                                                }
                                                style={{
                                                    marginTop: "4px",
                                                    border: "none",
                                                    background: "none",
                                                    color: "#2563eb",
                                                    padding: 0,
                                                    cursor: "pointer"
                                                }}
                                            >
                                                {isExpanded ? "See less" : "See more"}
                                            </button>
                                        )}
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        {(() => {
                                            const rmp = getRmpData(course.professor);
                                            return (
                                                <>
                                                    <div>{course.professor ?? "TBA"}</div>
                                                    {rmp && rmp.numRatings > 0 && (
                                                        <div style={{ display: "flex", gap: "6px", marginTop: "5px", flexWrap: "wrap" }}>
                                                            <span
                                                                title="Quality rating from RateMyProfessors"
                                                                style={{
                                                                    display: "inline-flex",
                                                                    alignItems: "center",
                                                                    gap: "3px",
                                                                    fontSize: "11px",
                                                                    fontWeight: 600,
                                                                    padding: "2px 6px",
                                                                    borderRadius: "4px",
                                                                    backgroundColor: rmp.avgRating >= 4 ? "#dcfce7" : rmp.avgRating >= 3 ? "#fef9c3" : "#fee2e2",
                                                                    color: rmp.avgRating >= 4 ? "#166534" : rmp.avgRating >= 3 ? "#854d0e" : "#991b1b",
                                                                    border: `1px solid ${rmp.avgRating >= 4 ? "#86efac" : rmp.avgRating >= 3 ? "#fde047" : "#fca5a5"}`
                                                                }}
                                                            >
                                                                Quality: {rmp.avgRating?.toFixed(1) ?? "—"} /5.0
                                                            </span>
                                                            <span
                                                                title="Difficulty rating from RateMyProfessor"
                                                                style={{
                                                                    display: "inline-flex",
                                                                    alignItems: "center",
                                                                    gap: "3px",
                                                                    fontSize: "11px",
                                                                    fontWeight: 600,
                                                                    padding: "2px 6px",
                                                                    borderRadius: "4px",
                                                                    backgroundColor: rmp.avgDifficulty <= 2.5 ? "#dcfce7" : rmp.avgDifficulty <= 3.5 ? "#fef9c3" : "#fee2e2",
                                                                    color: rmp.avgDifficulty <= 2.5 ? "#166534" : rmp.avgDifficulty <= 3.5 ? "#854d0e" : "#991b1b",
                                                                    border: `1px solid ${rmp.avgDifficulty <= 2.5 ? "#86efac" : rmp.avgDifficulty <= 3.5 ? "#fde047" : "#fca5a5"}`
                                                                }}
                                                            >
                                                                Difficulty: {rmp.avgDifficulty?.toFixed(1) ?? "—"} /5.0
                                                            </span>
                                                        </div>
                                                    )}
                                                </>
                                            );
                                        })()}
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        {formatDays(course)}
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        {formatMeetingTimes(course)}
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        {course.credits}
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        {course.semester}
                                    </td>

                                    <td style={{ padding: "10px" }}>
                                        {course.isOpen ? (
                                            schedule.some(
                                                (c) =>
                                                    c.courseCode === course.courseCode &&
                                                    c.semester === course.semester
                                            ) ? (
                                                <button
                                                    onClick={() =>
                                                        removeCourse(
                                                            course.courseCode,
                                                            course.semester
                                                        )
                                                    }
                                                    style={{
                                                        backgroundColor: "#dc2626",
                                                        color: "white",
                                                        border: "none",
                                                        padding: "6px 12px",
                                                        borderRadius: "4px",
                                                        cursor: "pointer"
                                                    }}
                                                >
                                                    Remove
                                                </button>
                                            ) : (
                                                <button
                                                    onClick={() =>
                                                        addCourse(
                                                            course.courseCode,
                                                            course.semester
                                                        )
                                                    }
                                                    style={{
                                                        backgroundColor: "#1d54a1",
                                                        color: "white",
                                                        border: "none",
                                                        padding: "6px 12px",
                                                        borderRadius: "4px",
                                                        cursor: "pointer"
                                                    }}
                                                >
                                                    Add
                                                </button>
                                            )
                                        ) : null}
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
export default SearchPage;