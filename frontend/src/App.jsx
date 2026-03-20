import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import SearchPage from "./pages/SearchPage";
import CalendarPage from "./pages/CalendarPage";
import ProfilePage from "./pages/ProfilePage";

function App() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [filters, setFilters] = useState({
    department: "",
    professor: "",
    credits: "",
    days: [],
    startTime: "",
    endTime: ""
  });
  const [expandedDescriptions, setExpandedDescriptions] = useState({});
  const [selectedSemester, setSelectedSemester] = useState("");

  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route
            path="/"
            element={
              <SearchPage
                query={query} setQuery={setQuery}
                results={results} setResults={setResults}
                filters={filters} setFilters={setFilters}
                expandedDescriptions={expandedDescriptions} setExpandedDescriptions={setExpandedDescriptions}
                selectedSemester={selectedSemester} setSelectedSemester={setSelectedSemester}
              />
            }
          />
          <Route path="/calendar" element={<CalendarPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;