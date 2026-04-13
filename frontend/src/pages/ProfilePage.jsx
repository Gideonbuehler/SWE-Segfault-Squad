function ProfilePage({ darkMode, setDarkMode }) {
  return (
    <div>
      <h1>Profile</h1>
      <div className="card">
        <p>User information will appear here.</p>
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