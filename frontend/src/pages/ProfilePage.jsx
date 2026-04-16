import { useState, useEffect } from "react";

function ProfilePage({ darkMode, setDarkMode }) {
  const [profile, setProfile] = useState(null);
  const[formData, setFormData] = useState(null);
  const[y, setYear] = useState("");
  const[ma, setMajor] = useState("");
  const[mi, setMinor] = useState([]);
  const[cc, setCompletedCourses] = useState([]);
  const handleSubmit = (e) => {
    e.preventDefault();
    if (formData.major.trim() === "") return;

  setProfile(prev => ({
    ...prev,
    major: formData.major
  }));
  };

   const fetchProfile = async () => {
    
    const response = await fetch(`/api/profile/`, { method: "GET" });
    const data = await response.json();
    setProfile(data);
    setFormData(profile);
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  return (
    <div>
      <h1>Profile</h1>
      <div className="card">
        <h1>Major: {profile?.major}</h1>
        <h1>Minors: {profile?.minors}</h1>

      </div>
      <div className="card">
        <p>User information will appear here.</p>
        <form onSubmit={handleSubmit}>
          <div style={{marginBottom: "7px"}}>
          <label> Change Year</label>
          <select
          value={formData?.year || ""}
          onChange={(e) => setFormData(prev => ({
      ...prev,
      year: e.target.value
    }))
  }
        > 
          <option value="">Choose a year</option>
          <option value="FR">FRESHMAN</option>
          <option value="SO">SOPHOMORE</option>
          <option value="JR">JUNIOR</option>
          <option value="SR">SENIOR</option>
          </select>
          </div>
          <br />
          <div style={{marginBottom: "7px"}}>
          <label>Update Major</label>
          <input 
          type="text"
          value={formData?.major || ""}
           onChange={(e) => {
            
            
            
              setFormData(prev => ({
      ...prev,
      major: e.target.value.toUpperCase()
    }));
  }
  
  }
          />
          </div>
        
          <br />
          <div style={{marginBottom: "7px"}}>
          <label> Add Minor</label>
          <input 
          type="text"
          value={formData?.minor || ""}
          onChange={(e) => setFormData(prev => ({
      ...prev,
      minor: e.target.value
    }))

  }

          />
          {/* Make an add to completed courses on search page? */}
          {/* <label> Add Completed Course</label>
          <input 
          type="text"
          value={profile?.compltedCourses}
          onChange={(e) => setFormData(prev => ({
      ...prev,
      compltedCourses: e.target.value
    }))
  }
          /> */}
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