const express = require('express');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

mongoose.connect(process.env.MONGODB_URI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.error('MongoDB connection error:', err));

const UserSchema = new mongoose.Schema({
  first_name: String,
  last_name: String,
  phone: String,
  email: String,
  password: String,
  role: String,
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now },
});

const User = mongoose.model('User', UserSchema);

app.get('/api/users', async (req, res) => {
  try {
    const users = await User.find();
    res.json(users);
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

app.post('/api/users', async (req, res) => {
  const { first_name, last_name, phone, email, password, role } = req.body;
  try {

    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new User({ first_name, last_name, phone, email, password: hashedPassword, role });
    await newUser.save();
    res.status(201).json(newUser);
  } catch (err) {
    res.status(500).json({ message: 'Error creating user' });
  }
});

app.put('/api/users/:id', async (req, res) => {
  const { first_name, last_name, phone, email, role } = req.body;
  try {
    const user = await User.findByIdAndUpdate(req.params.id, { first_name, last_name, phone, email, role }, { new: true });
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: 'Error updating user' });
  }
});

app.delete('/api/users/:id', async (req, res) => {
  try {
    await User.findByIdAndDelete(req.params.id);
    res.json({ message: 'User deleted' });
  } catch (err) {
    res.status(500).json({ message: 'Error deleting user' });
  }
});

app.listen(5000, () => {
  console.log('Server is running on http://localhost:5173');
});

app.post('/api/users', async (req, res) => {
    try {
      const { first_name, last_name, email, phone, password, role } = req.body;
  
      if (!first_name || !last_name || !email || !phone || !password || !role) {
        return res.status(400).json({ message: 'All fields are required' });
      }
  
      const hashedPassword = await bcrypt.hash(password, 10);
      const newUser = new User({ first_name, last_name, email, phone, password: hashedPassword, role });
  
      await newUser.save();
      res.status(201).json({ message: 'User created successfully', user: newUser });
    } catch (error) {
      res.status(500).json({ message: 'Error creating user', error });
    }
  });
  
  app.post('/api/signin', async (req, res) => {
    const { email, password } = req.body;
  
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(400).json({ message: 'Invalid email or password' });
    }
  
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).json({ message: 'Invalid email or password' });
    }
  
    const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, { expiresIn: '1h' });
    res.json({ token });
  });
