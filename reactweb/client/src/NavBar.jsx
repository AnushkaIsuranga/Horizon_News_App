import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user'); // Remove user info
    navigate('/');
  };

  return (
    <nav className="navbar">
      <h2 className="navbar-title">Admin Panel</h2>
      <div className="nav-links">
        <Link to="/home">Home</Link>
        <Link to="/add-user">Add User</Link>
        <button onClick={handleLogout} className="logout-btn">Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;
