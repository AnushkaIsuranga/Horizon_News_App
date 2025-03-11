import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <h2>Admin Panal</h2>
      <div className="nav-links">
        <Link to="/home">Home</Link>
        <Link to="/add-user">Add User</Link>
      </div>
    </nav>
  );
};

export default Navbar