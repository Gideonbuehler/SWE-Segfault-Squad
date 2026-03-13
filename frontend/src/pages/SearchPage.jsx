import { useState } from "react";

function SearchPage() {

  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

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

    const response = await fetch(`/api/searchResults/${query}`, {
      method: "POST"
    });

   const data = await response.json();
   console.log(data);
   if (response.ok) {
       setResults(data.results);
       console.log(results[0].startTime)
   } else {
       console.error("Backend error:", data);
       setResults([]);
   }
  };

  const addCourse = async (courseCode) => {
    const response = await fetch(`/api/mySchedule/add/${courseCode}`, {
      method: "POST"
    });

    if (response.ok) {
      alert(`${courseCode} added to schedule!`);
    } else {
      alert("Failed to add course. It may conflict with an existing course.");
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

      <button onClick={runSearch}>Search</button>

      <div className="card">
        {results.map((course, index) => (
          <div key={index} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
            <span>
               <b>{course.courseCode}</b> - {course.courseName} - {course.semester} - {course.professor} - {course.days}({formatTime(course.startTime)} - {formatTime(course.endTime)})
            </span>
            <button onClick={() => addCourse(course.courseCode)}>
              Add
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default SearchPage;