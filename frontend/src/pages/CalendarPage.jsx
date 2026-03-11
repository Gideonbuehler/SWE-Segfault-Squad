import FullCalendar from "@fullcalendar/react";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";

function CalendarPage() {

  const events = [
    {
      title: "Algorithms",
      daysOfWeek: [1],
      startTime: "09:00",
      endTime: "10:30"
    },
    {
      title: "Databases",
      daysOfWeek: [2],
      startTime: "11:00",
      endTime: "12:30"
    },
    {
      title: "Software Engineering",
      daysOfWeek: [3],
      startTime: "13:00",
      endTime: "14:30"
    }
  ];

  return (
    <div>
      <h1>Weekly Schedule</h1>

      <div className="card">
        <FullCalendar
          plugins={[timeGridPlugin, interactionPlugin]}
          initialView="timeGridWeek"

          /* hide navigation */
          headerToolbar={false}

          /* only show weekday names */
          dayHeaderFormat={{ weekday: "short" }}

          /* hide weekends if desired */
//           weekends={false}

          /* course hours */
          slotMinTime="08:00:00"
          slotMaxTime="20:00:00"

          /* styling */
          allDaySlot={false}
          height="auto"

          events={events}
        />
      </div>
    </div>
  );
}

export default CalendarPage;