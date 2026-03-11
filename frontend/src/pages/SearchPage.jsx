import { useState } from "react";

function SearchPage() {

  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  const runSearch = async () => {

    const response = await fetch(`/api/searchResults/${query}`, {
      method: "POST"
    });

   const data = await response.json();
   console.log(data);
   if (response.ok) {
       setResults(data.results);
   } else {
       console.error("Backend error:", data);
       setResults([]);
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
          <div key={index}>
            <b>{course.courseCode}</b> - {course.courseName} - {course.semester} - {course.professor} - ({course.startTime} - {course.endTime})
          </div>
        ))}
      </div>
    </div>
  );
}

export default SearchPage;