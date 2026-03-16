import { useState } from "react";

function SearchPage() {

  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  const [filters, setFilters] = useState({
      department: "",
      professor: "",
      credits: "",
      days: []
  });

  const [schedule, setSchedule] = useState([]);

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
    const hour = time[0];
    const minute = time[1];
    const period = hour >= 12 ? "PM" : "AM";
    const hour12 = hour % 12 || 12;
    const min = minute.toString().padStart(2, "0");
    return `${hour12}:${min} ${period}`;
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
        setResults(sortByCourseCode(Array.from(data.results ?? [])));
      }
    } else {
      setResults([]);
    }
  };

  const runFilter = async () => {
    const params = new URLSearchParams();
    if (filters.department) params.append("department", filters.department);
    if (filters.professor) params.append("professor", filters.professor);
    if (filters.credits) params.append("credits", filters.credits);
    if (filters.days.length > 0) params.append("days", filters.days.join(","));

    const response = await fetch(`/api/searchResults/${query}/filter?${params}`, {
      method: "POST"
    });

    if (response.ok) {
      const data = await response.json();
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
          setFilters({ department: "", professor: "", credits: "", days: [] });
          runSearch();
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

        </div>
      </div>

      <div className="card">
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ backgroundColor: "#1f2937", color: "white", textAlign: "left" }}>
              <th style={{ padding: "10px" }}>Code</th>
              <th style={{ padding: "10px" }}>Name</th>
              <th style={{ padding: "10px" }}>Professor</th>
              <th style={{ padding: "10px" }}>Days</th>
              <th style={{ padding: "10px" }}>Time</th>
              <th style={{ padding: "10px" }}>Credits</th>
              <th style={{ padding: "10px" }}>Semester</th>
              <th style={{ padding: "10px" }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {results.map((course, index) => (
              <tr key={index} style={{ borderBottom: "1px solid #e5e7eb", backgroundColor: index % 2 === 0 ? "#f9fafb" : "white" }}>
                <td style={{ padding: "10px" }}><b>{course.courseCode}</b></td>
                <td style={{ padding: "10px" }}>{course.courseName}</td>
                <td style={{ padding: "10px" }}>{course.professor}</td>
                <td style={{ padding: "10px" }}>{course.days?.join(", ")}</td>
                <td style={{ padding: "10px" }}>{formatTime(course.startTime)} - {formatTime(course.endTime)}</td>
                <td style={{ padding: "10px" }}>{course.credits}</td>
                <td style={{ padding: "10px" }}>{course.semester}</td>
                <td style={{ padding: "10px" }}>
                  {schedule.some(c => c.courseCode === course.courseCode)
                    ? <button onClick={() => removeCourse(course.courseCode)} style={{ backgroundColor: "#dc2626", color: "white", border: "none", padding: "6px 12px", borderRadius: "4px", cursor: "pointer" }}>Remove</button>
                    : <button onClick={() => addCourse(course.courseCode)} style={{ backgroundColor: "#1f2937", color: "white", border: "none", padding: "6px 12px", borderRadius: "4px", cursor: "pointer" }}>Add</button>
                  }
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default SearchPage;