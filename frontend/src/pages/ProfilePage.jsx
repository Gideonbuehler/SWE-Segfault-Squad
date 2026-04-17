import { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

function ProfilePage({ darkMode, setDarkMode }) {
  const [profile, setProfile] = useState(null);
  const[formData, setFormData] = useState(null);
  //I wrote my own logic for handleSubmit, but it was extreamly broken. Started trying
  //to diagnose it with AI and write my own fix. Eventually just copied the handleSubmit
  //that AI generated, though based on my origional work.
 const handleSubmit = async (e) => {
  e.preventDefault();
  if (!formData) return;

  try {
    let hasChanges = false;

    // Update Major
    if (formData.major && formData.major.trim() !== "") {
      const majorValue = formData.major.trim().toUpperCase();
      const res = await fetch(`/api/profile/major/${encodeURIComponent(majorValue)}`, {
        method: "POST",
      });

      if (res.ok) {
        hasChanges = true;
      } else {
        console.error("Major update failed:", await res.text());
      }
    }

    // Update Year
    if (formData.year) {
      const res = await fetch(`/api/profile/year/${encodeURIComponent(formData.year)}`, {
        method: "POST",
      });

      if (res.ok) {
        hasChanges = true;
      } else {
        console.error("Year update failed:", await res.text());
      }
    }

    // Only refresh from backend if we actually tried to make changes
    if (hasChanges) {
      await fetchProfile();        // Now safe to refresh
    }

  } catch (error) {
    console.error("Submit error:", error);
  }
};

  const fetchProfile = async () => {
    const response = await fetch(`/api/profile/`, { method: "GET" });
    const data = await response.json();
    setProfile(data);
    setFormData(data);
  };

  useEffect(() => {
    fetchProfile();
  }, []);
  
  return (
    <div>
      <h1>Profile</h1>
      <div className="card">
        <h2>Year: {profile?.year}</h2>
        <h2>Major: {profile?.major}</h2>
        <h2>Minor: {profile?.minors}</h2>
        <h2>Completed Courses: {profile?.completedCourses}</h2>
      </div>
      <div className="card">
        <p>User information will appear here.</p>
        <form onSubmit={handleSubmit}>
          <label>Update Major</label>
          <input 
          type="text"
          value={formData?.major || ""}
          onChange={(e) => setFormData(prev => ({
      ...prev,
      major: e.target.value.toUpperCase()
    }))
}
          />
          <br />
          <label>Update Minor</label>
          <input 
          type="text"
          value={formData?.minors || ""}
          onChange={(e) => setFormData(prev => ({
      ...prev,
      minors: e.target.value.toUpperCase()
    }))
}
          />
          <br />
          <div style={{marginBottom: "7px"}}>
          <label> Change Year</label>
          <select
          value={formData?.year}
          onChange={(e) => setFormData(prev => ({
      ...prev,
      year: e.target.value
    }))
  }
        > 
          <option value="">Choose a year</option>
          <option value="FRESHMAN">FRESHMAN</option>
          <option value="SOPHOMORE">SOPHOMORE</option>
          <option value="JUNIOR">JUNIOR</option>
          <option value="SENIOR">SENIOR</option>
          </select>
          </div>

          
          <br />
           <button type="submit" style={{
          marginTop: "16px",
          padding: "8px 16px",
          borderRadius: "4px",
          border: "none",
          cursor: "pointer"}}>Submit</button>

          
        </form>
          
      </div>
      <button
        onClick={() => setDarkMode(prev => !prev)}
        style={{
          marginTop: "16px",
          padding: "8px 16px",
          borderRadius: "4px",
          border: "none",
          cursor: "pointer",
          backgroundColor: darkMode ? "#f9fafb" : "#1f2937",
          color: darkMode ? "#1f2937" : "#f9fafb"
        }}
      >
        {darkMode ? "☀️ Light Mode" : "🌙 Dark Mode"}
      </button>
    </div>
  );
}
export default ProfilePage;