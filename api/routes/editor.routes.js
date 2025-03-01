const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const Editor = require('../models/editor.model');

const router = express.Router();

router.post('/register', async (req, res) => {
    try {
        const { first_name, last_name, email, password } = req.body;

        if (!first_name || !last_name || !email || !password) {
            return res.status(400).json({ message: "All fields are required" });
        }

        // Check if admin already exists
        const existingAdmin = await Admin.findOne({ email });
        if (existingAdmin) {
            return res.status(400).json({ message: "Editor already exists" });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        const admin = new Admin({
            first_name,
            last_name,
            email,
            password: hashedPassword
        });

        await admin.save();
        res.status(201).json({ message: "Editor created successfully" });

    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const admin = await Admin.findOne({ email });
        if (!admin) return res.status(404).json({ message: "Editor not found" });

        const isMatch = await bcrypt.compare(password, admin.password);
        if (!isMatch) return res.status(400).json({ message: "Invalid credentials" });

        const token = jwt.sign({ id: admin._id, role: 'Editor' }, 'your_secret_key', { expiresIn: '7d' });

        res.json({ token, admin });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

const isEditor = (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader) return res.status(401).json({ message: 'Unauthorized' });

    const token = authHeader.split(' ')[1];
    jwt.verify(token, 'your_secret_key', (err, decoded) => {
        if (err) return res.status(403).json({ message: 'Forbidden' });
        if (decoded.role !== 'Editor') return res.status(403).json({ message: 'Editor access only' });

        req.admin = decoded;
        next();
    });
};

module.exports = { router, isAdmin };
