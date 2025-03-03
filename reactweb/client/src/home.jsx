import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './Home.css'; 

const Home = () => {
  const [users, setUsers] = useState([]);
  const [newUser, setNewUser] = useState({
    first_name: '',
    last_name: '',
    email: '',
    phone: '',
    password: '',
    role: 'editor'
  });

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

  const addUser = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:5000/api/users', newUser);
      setUsers([...users, response.data.user]);
      setNewUser({ first_name: '', last_name: '', email: '', phone: '', password: '', role: 'editor' });
    } catch (error) {
      console.error('Error adding user:', error);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>User List</h1>

      <form onSubmit={addUser}>
        <input type="text" placeholder="First Name" value={newUser.first_name} onChange={(e) => setNewUser({ ...newUser, first_name: e.target.value })} required />
        <input type="text" placeholder="Last Name" value={newUser.last_name} onChange={(e) => setNewUser({ ...newUser, last_name: e.target.value })} required />
        <input type="email" placeholder="Email" value={newUser.email} onChange={(e) => setNewUser({ ...newUser, email: e.target.value })} required />
        <input type="text" placeholder="Phone" value={newUser.phone} onChange={(e) => setNewUser({ ...newUser, phone: e.target.value })} required />
        <input type="password" placeholder="Password" value={newUser.password} onChange={(e) => setNewUser({ ...newUser, password: e.target.value })} required />
        <select value={newUser.role} onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}>
          <option value="reporter">reporter</option>
          <option value="user">user</option>
        </select>
        <button type="submit">Add User</button>
      </form>

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
  );
};

export default Home;
