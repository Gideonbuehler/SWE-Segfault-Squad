import { useState } from "react";

function SearchPage() {

  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [expandedDescriptions, setExpandedDescriptions] = useState({});

  const [filters, setFilters] = useState({
      department: "",
      professor: "",
      credits: "",
      days: [],
      startTime: "",
      endTime: ""
  });

  const [schedule, setSchedule] = useState([]);

  const DESCRIPTION_PREVIEW_LENGTH = 100;

  const fetchSchedule = async () => {
    const response = await fetch("/api/mySchedule");
    const data = await response.json();
    setSchedule(data.courses ?? []);
  };

  const sortByCourseCode = (courses) => {
    return [...courses].sort((a, b) => a.courseCode.localeCompare(b.courseCode));
  };

  const formatTime = (time) => {
    if (!time) return "TBA";
    let hour;
    let minute;

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

  const getMeetings = (course) => Object.entries(course.dayTimeMap ?? {});

  const formatDays = (course) => {
    const days = getMeetings(course).map(([day]) => day);
    return days.length > 0 ? days.join(", ") : "TBA";
  };

  const formatMeetingTimes = (course) => {
    const ranges = getMeetings(course)
      .map(([, range]) => {
        if (!Array.isArray(range)) return null;
        return `${formatTime(range[0])} - ${formatTime(range[1])}`;
      })
      .filter(Boolean);

    const uniqueRanges = [...new Set(ranges)];
    return uniqueRanges.length > 0 ? uniqueRanges.join("; ") : "TBA";
  };

  const getDescription = (course) => (course.description ?? "").trim();

  const toggleDescription = (courseKey) => {
    setExpandedDescriptions((prev) => ({
      ...prev,
      [courseKey]: !prev[courseKey]
    }));
  };

  const runSearch = async () => {
    await fetchSchedule();
    const response = await fetch(`/api/searchResults/${query}`, { method: "POST" });
    const data = await response.json();

    if (response.ok) {
      const hasFilters = filters.department || filters.professor || filters.credits || filters.days.length > 0;
      if (hasFilters) {
        await runFilter();
      } else {
        setExpandedDescriptions({});
        setResults(sortByCourseCode(Array.from(data.results ?? [])));
      }
    } else {
      setExpandedDescriptions({});
      setResults([]);
    }
  };

  const clearFilter = async () => {
  await fetchSchedule();
  const response = await fetch(`/api/noFilters`, { method: "GET" });
  const data = await response.json();

  if (response.ok) {
    setFilters({ department: "", professor: "", credits: "", days: [], startTime: "", endTime: "" });
    setExpandedDescriptions({});
    setResults(sortByCourseCode(Array.from(data ?? [])));
  } else {
    setExpandedDescriptions({});
    setResults([]);
  }
};

const runFilter = async () => {
  const hasFilters = filters.department || filters.professor || filters.credits || filters.days.length > 0 || filters.startTime || filters.endTime;
  
  if (!hasFilters) {
    await clearFilter(); // fall back to original results
    return;
  }

  const params = new URLSearchParams();
  if (filters.department) params.append("department", filters.department);
  if (filters.professor) params.append("professor", filters.professor);
  if (filters.credits) params.append("credits", filters.credits);
  if (filters.days.length > 0) params.append("days", filters.days.join(","));
  if (filters.startTime) params.append("startTime", filters.startTime);
  if (filters.endTime) params.append("endTime", filters.endTime);

  const response = await fetch(`/api/filterResults/${query}/filter?${params}`, {
    method: "POST"
  });

  if (response.ok) {
    const data = await response.json();
    setExpandedDescriptions({});
    setResults(sortByCourseCode(Array.from(data)));
  }
};

  const addCourse = async (courseCode) => {
    const response = await fetch(`/api/mySchedule/add/${courseCode}`, {
      method: "POST"
    });

    if (response.ok) {
      alert(`${courseCode} added to schedule!`);
      await fetchSchedule();
    } else {
      alert("Failed to add course. It may conflict with an existing course.");
    }
  };
  const removeCourse = async (courseCode) => {
    const response = await fetch(`/api/mySchedule/remove/${courseCode}`, {
      method: "DELETE"
    });

    if (response.ok) {
      alert(`${courseCode} removed from schedule!`);
      await fetchSchedule();
    } else {
      alert("Failed to remove course.");
    }
  };


  return (
    <div>
      <h1>Search</h1>

      <input
        type="text"
        placeholder="Search courses..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />

      <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
        <button onClick={runSearch}>Search</button>
        <button onClick={runFilter}>Refresh Filters</button>
        <button onClick={() => {
          setFilters({ department: "", professor: "", credits: "", days: [], startTime: "", endTime: "" });
          clearFilter();
        }}>Clear Filters</button>
      </div>

      <div className="card" style={{ marginBottom: "10px" }}>
        <h3 style={{ marginTop: 0 }}>Filters</h3>
        <div style={{ display: "flex", gap: "20px", flexWrap: "wrap" }}>

          <label>Department:
            <input
              type="text"
              placeholder="e.g. COMP"
              value={filters.department}
              onChange={(e) => setFilters({ ...filters, department: e.target.value })}
              style={{ marginLeft: "8px" }}
            />
          </label>

          <label>Professor:
            <input
              type="text"
              placeholder="e.g. Wolfe"
              value={filters.professor}
              onChange={(e) => setFilters({ ...filters, professor: e.target.value })}
              style={{ marginLeft: "8px" }}
            />
          </label>

          <label>Credits:
            <input
              type="number"
              placeholder="e.g. 3"
              value={filters.credits}
              onChange={(e) => setFilters({ ...filters, credits: e.target.value })}
              style={{ marginLeft: "8px", width: "60px" }}
            />
          </label>

          <label>Days:
            {["M", "T", "W", "R", "F"].map(day => (
              <label key={day} style={{ marginLeft: "8px" }}>
                <input
                  type="checkbox"
                  checked={filters.days.includes(day)}
                  onChange={(e) => {
                    const updated = e.target.checked
                      ? [...filters.days, day]
                      : filters.days.filter(d => d !== day);
                    setFilters({ ...filters, days: updated });
                  }}
                /> {day}
              </label>
            ))}
          </label>
          <label>Start Time:
              <input
              type="text"
              placeholder="e.g. 10:00"
              value={filters.startTime}
              onChange={(e) => setFilters({ ...filters, startTime: e.target.value })}
              style={{ marginLeft: "8px", width: "80px" }}
              />
          </label>

          <label>End Time:
            <input
            type="text"
            placeholder="e.g. 10:50"
            value={filters.endTime}
            onChange={(e) => setFilters({ ...filters, endTime: e.target.value })}
            style={{ marginLeft: "8px", width: "80px" }}
            />
          </label>

        </div>
      </div>

      <div className="card">
        <table style={{ width: "100%", borderCollapse: "collapse", tableLayout: "fixed" }}>
          <thead>
            <tr style={{ backgroundColor: "#1f2937", color: "white", textAlign: "left" }}>
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
            {results.map((course, index) => {
              const courseKey = `${course.courseCode}-${index}`;
              const description = getDescription(course);
              const isExpanded = Boolean(expandedDescriptions[courseKey]);
              const hasLongDescription = description.length > DESCRIPTION_PREVIEW_LENGTH;

              return (
                <tr key={index} style={{ borderBottom: "1px solid #e5e7eb", backgroundColor: index % 2 === 0 ? "#f9fafb" : "white" }}>
                  <td style={{ padding: "10px" }}><b>{course.courseCode}</b></td>
                  <td style={{ padding: "10px" }}>
                    <div>{course.courseName}</div>
                    <div
                      style={{
                        marginTop: "4px",
                        overflowWrap: "anywhere",
                        ...(isExpanded
                          ? {}
                          : {
                              display: "-webkit-box",
                              WebkitLineClamp: 2,
                              WebkitBoxOrient: "vertical",
                              overflow: "hidden"
                            })
                      }}
                    >
                      {description || "No description available."}
                    </div>
                    {hasLongDescription && (
                      <button
                        type="button"
                        onClick={() => toggleDescription(courseKey)}
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
                  <td style={{ padding: "10px" }}>{course.professor}</td>
                  <td style={{ padding: "10px" }}>{formatDays(course)}</td>
                  <td style={{ padding: "10px" }}>{formatMeetingTimes(course)}</td>
                  <td style={{ padding: "10px" }}>{course.credits}</td>
                  <td style={{ padding: "10px" }}>{course.semester}</td>
                  <td style={{ padding: "10px" }}>
                    {schedule.some(c => c.courseCode === course.courseCode)
                      ? <button onClick={() => removeCourse(course.courseCode)} style={{ backgroundColor: "#dc2626", color: "white", border: "none", padding: "6px 12px", borderRadius: "4px", cursor: "pointer" }}>Remove</button>
                      : <button onClick={() => addCourse(course.courseCode)} style={{ backgroundColor: "#1f2937", color: "white", border: "none", padding: "6px 12px", borderRadius: "4px", cursor: "pointer" }}>Add</button>
                    }
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