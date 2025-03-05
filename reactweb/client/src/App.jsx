import { useState } from 'react'
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Signin from './Signin.jsx';
import Home from './home.jsx';
import AddUser from './AddUser.jsx';
import NewsEditor from './NewsEditor.jsx';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Signin />} />
        <Route path="/home" element={<Home />} />
        <Route path="/add-user" element={<AddUser />} />
        <Route path="/news" element={<NewsEditor />} />
      </Routes>
    </Router>
  );
}

export default App