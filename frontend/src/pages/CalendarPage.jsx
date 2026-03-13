import { useEffect, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";

function CalendarPage() {

  const [events, setEvents] = useState([]);

  const dayToNumber = (day) => {
    const map = { "M": 1, "T": 2, "W": 3, "R": 4, "F": 5 };
    return map[day];
  };

  const formatTime = (timeArray) => {
    if (!timeArray) return null;
    const hour = timeArray[0].toString().padStart(2, "0");
    const minute = timeArray[1].toString().padStart(2, "0");
    return `${hour}:${minute}`;
  };

  useEffect(() => {
    const fetchCalendar = async () => {
      const response = await fetch("/api/calendar");
      const data = await response.json();

      const mapped = [];
      for (const block of data.blocks) {
        const course = block.course;
        if (!course || !course.days) continue;

        for (const day of course.days) {
          const dayNum = dayToNumber(day);
          if (dayNum === undefined) continue;

          mapped.push({
            title: course.courseName,
            daysOfWeek: [dayNum],
            startTime: formatTime(course.startTime),
            endTime: formatTime(course.endTime)
          });
        }
      }

      setEvents(mapped);
    };

    fetchCalendar();
  }, []);

  return (
    <div>
      <h1>Weekly Schedule</h1>

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
        />
      </div>
    </div>
  );
}

export default CalendarPage;