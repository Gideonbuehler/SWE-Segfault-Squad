import { useEffect, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";

function CalendarPage() {
  const [events, setEvents] = useState([]);
  const [selectedCourse, setSelectedCourse] = useState(null);

  const dayToNumber = (day) => {
    const map = { "M": 1, "T": 2, "W": 3, "R": 4, "F": 5 };
    return map[day];
  };

  const formatTime = (timeArray) => {
    if (!timeArray) return "TBA";
    let hour;
    let minute;

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

  const toCalendarTime = (timeArray) => {
    if (!timeArray) return null;

    let hour;
    let minute;

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

  const fetchCalendar = async () => {
    const response = await fetch("/api/calendar");
    const data = await response.json();

    const mapped = [];
    for (const block of data.blocks) {
      const course = block.course;
      if (!course || !course.dayTimeMap) continue;

      for (const [day, range] of Object.entries(course.dayTimeMap)) {
        const dayNum = dayToNumber(day);
        const start = Array.isArray(range) ? toCalendarTime(range[0]) : null;
        const end = Array.isArray(range) ? toCalendarTime(range[1]) : null;
        if (dayNum === undefined) continue;
        if (!start || !end) continue;

        mapped.push({
          title: course.courseName,
          daysOfWeek: [dayNum],
          startTime: start,
          endTime: end,
          extendedProps: { course, day, startTime: range[0], endTime: range[1] }
        });
      }
    }

    setEvents(mapped);
  };

 const downloadPDF = () => {
    window.open("/api/mySchedule/pdf", "_blank");
  };

  useEffect(() => {
    fetchCalendar();
  }, []);

  const handleEventClick = (info) => {
    const { course, day, startTime, endTime } = info.event.extendedProps;
    setSelectedCourse({ ...course, day, startTime, endTime });
  };

  const removeCourse = async () => {
    const response = await fetch(`/api/mySchedule/remove/${selectedCourse.courseCode}`, {
      method: "DELETE"
    });

    if (response.ok) {
      setSelectedCourse(null);
      await fetchCalendar();
    } else {
      alert("Failed to remove course.");
    }
  };

  return (
    <div>
      <h1>Weekly Schedule</h1>

      {/* Course info popup */}
      {selectedCourse && (
        <div className="card" style={{ marginBottom: "20px", backgroundColor: "#f0f4ff", border: "1px solid #1f2937" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
            <div>
              <h3 style={{ margin: "0 0 8px 0" }}>{selectedCourse.courseName}</h3>
              <p style={{ margin: "4px 0" }}><b>Code:</b> {selectedCourse.courseCode}</p>
              <p style={{ margin: "4px 0" }}><b>Professor:</b> {selectedCourse.professor}</p>
              <p style={{ margin: "4px 0" }}><b>Location:</b> {selectedCourse.location}</p>
              <p style={{ margin: "4px 0" }}><b>Time:</b> {formatTime(selectedCourse.startTime)} - {formatTime(selectedCourse.endTime)}</p>
              <p style={{ margin: "4px 0" }}><b>Days:</b> {selectedCourse.day}</p>
              <p style={{ margin: "4px 0" }}><b>Credits:</b> {selectedCourse.credits}</p>
            </div>
            <div style={{ display: "flex", gap: "10px" }}>
              <button
                onClick={removeCourse}
                style={{ backgroundColor: "#dc2626", color: "white", border: "none", padding: "8px 16px", borderRadius: "4px", cursor: "pointer" }}
              >
                Remove Course
              </button>
              <button
                onClick={() => setSelectedCourse(null)}
                style={{ backgroundColor: "#6b7280", color: "white", border: "none", padding: "8px 16px", borderRadius: "4px", cursor: "pointer" }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

     <button
      onClick={downloadPDF}
      style={{ backgroundColor: "#1f2937", color: "white", border: "none", padding: "8px 16px", borderRadius: "4px", cursor: "pointer", marginBottom: "10px" }}
    >
      Download Schedule PDF
    </button>

      <div className="card">
        <FullCalendar
          plugins={[timeGridPlugin, interactionPlugin]}
          initialView="timeGridWeek"
          headerToolbar={false}
          dayHeaderFormat={{ weekday: "short" }}
          weekends={false}
          slotMinTime="08:00:00"
          slotMaxTime="20:00:00"
          allDaySlot={false}
          height="auto"
          expandRows={true}
          events={events}
          eventClick={handleEventClick}
        />
      </div>
    </div>
  );
}

export default CalendarPage;