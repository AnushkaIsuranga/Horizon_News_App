import logo from './assets/logo.png';
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Signin.css';

function Signin() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await axios.post('http://localhost:5000/api/signin', { email, password });
      const { token } = response.data;

      localStorage.setItem('token', token);
      navigate('/home');
    } catch (err) {
      setError('Invalid email or password');
    }
  };

  return (
    <div className="container">
      <div className="card">
        <img src={logo} className="img" alt="logo" />
        <h2 className="title">Admin Panel</h2>
        <form onSubmit={handleSubmit}>
          <label className="sechead">Email</label><br />
          <input
            type="email"
            className="emailin"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          /><br />
          <label className="sechead">Password</label><br />
          <input
            type="password"
            className="parsin"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          /><br />
          <button type="submit">Sign In</button>
          {error && <p>{error}</p>}
        </form>
      </div>
    </div>
  );
}

export default Signin;
