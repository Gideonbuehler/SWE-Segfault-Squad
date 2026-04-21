import { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import SearchPage from "./pages/SearchPage";
import CalendarPage from "./pages/CalendarPage";
import ProfilePage from "./pages/ProfilePage";
import { ToastProvider } from "./components/useToast.jsx";
function App() {

  // Put queries, results, and filters here to ensure they stay even when leaving Schedule page.
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
  const [schedule, setSchedule] = useState([]);
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem("darkMode") === "true";
  });


  const fetchSchedule = async () => {
    const response = await fetch("/api/mySchedule");
    const data = await response.json();
    setSchedule(data.courses ?? []);
  };

  useEffect(() => {
    document.documentElement.classList.toggle("dark", darkMode);
    localStorage.setItem("darkMode", darkMode);
  }, [darkMode]);

  useEffect(() => {
    fetchSchedule();
  }, []);

  return (
    <BrowserRouter>
      <ToastProvider>
      <Layout>
        <Routes>
          <Route
            path="/"
            element={
              <SearchPage
              // Sets variables in Search page when loaded.
                query={query} setQuery={setQuery}
                results={results} setResults={setResults}
                filters={filters} setFilters={setFilters}
                expandedDescriptions={expandedDescriptions} setExpandedDescriptions={setExpandedDescriptions}
                selectedSemester={selectedSemester} setSelectedSemester={setSelectedSemester}
                schedule={schedule} fetchSchedule={fetchSchedule}
                darkMode={darkMode}
            />
            }
          />
          <Route path="/calendar" element={<CalendarPage />} />
          <Route path="/profile" element={<ProfilePage darkMode={darkMode} setDarkMode={setDarkMode} />} />
        </Routes>
      </Layout>
      </ToastProvider>
    </BrowserRouter>
  );
}

export default App;