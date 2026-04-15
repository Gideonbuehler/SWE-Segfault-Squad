import {useState} from "react";

function ProfilePage({ darkMode, setDarkMode }) {
  const [profile, setProfile] = useState(null);
  const [major, setMajor] = useState("");
  const [minor, setMinor] = useState("");
  const handleSubmit = (e) => {
    e.preventDefault();
  };

  useEffect(() => {
    fetch(`/api/profile/`)
      .then(res => res.json())
      .then(data => setProfile(data))
      .catch(err => console.error(err));
  }, []);


  
  return (
    <div>
      <h1>Profile</h1>
      <div className="card">
      <h2>Major: `${profile.major}`</h2>

      </div>
      <div className="card">
        <p>User information will appear here.</p>
        <form onSubmit={handleSubmit}>
          <label>Update Major</label>
          <input 
          type="text"
          value={major}
          onChange={(e) => setMajor(e.target.value)}
          />
          <label> Add Minor</label>
          <input 
          type="text"
          value={minor}
          onChange={(e) => setMinor(e.target.value)}
          />
          <label> Remove Minor</label>
          <select 
          value={minor}
          onChange={(e) => setMinor(e.target.value)}
          >
            <option value="Choose a Minor to Remove"></option>

            </select>
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