import { Link } from "react-router-dom";
import "../styles/global.css";

function Navbar() {
  return (
    <nav className="navbar">
      <h2 className="logo">MyApp</h2>

      <div className="nav-links">
        <Link to="/">Search</Link>
        <Link to="/calendar">Calendar</Link>
        <Link to="/profile">Profile</Link>
      </div>
    </nav>
  );
}

export default Navbar;