import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Navbar from './NavBar.jsx';
import './common.css';

const Home = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:5000/api/users');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const deleteUser = async (id) => {
    try {
      await axios.delete(`http://localhost:5000/api/users/${id}`);
      setUsers(users.filter(user => user._id !== id));
    } catch (error) {
      console.error('Error deleting user:', error);
    }
  };

  const updateUser = async (id) => {
    const newFirstName = prompt('Enter new first name:');
    if (!newFirstName) return;

    try {
      const response = await axios.put(`http://localhost:5000/api/users/${id}`, { first_name: newFirstName });
      setUsers(users.map(user => (user._id === id ? response.data : user)));
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  return (
    <div>
      <Navbar />
      <div style={{ padding: '20px' }}>
        <h1>User List</h1>
        <table border="1">
          <thead>
            <tr>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user._id}>
                <td>{user.first_name}</td>
                <td>{user.last_name}</td>
                <td>{user.email}</td>
                <td>{user.phone}</td>
                <td>{user.role}</td>
                <td className="actions">
                  <button onClick={() => updateUser(user._id)}>Edit</button>
                  <button onClick={() => deleteUser(user._id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Home;
