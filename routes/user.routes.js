const express = require('express');
const multer = require('multer');
const User = require('../models/user.model');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const router = express.Router();
const app = express();

// Multer setup for profile pictures
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + '-' + file.originalname);
    }
});
const upload = multer({ storage });

//Get All Users
router.get('/', async (req, res) => {
    try {
        const users = await User.find();
        res.send(users);
    } catch (error) {
        res.status(500).send(error);
    }
});

//Get a Single User
router.get('/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const user = await User.findById(req.params.id);
        res.status(200).json(user);
    } catch (error) {
        res.status(500).send(error);
    }
});

//Update a User
router.put('/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const user = await User.findByIdAndUpdate(id, req.body);

        if(!user) return res.status(404).send("User not found");

        const updatedUser = await User.findById(id);
        res.status(200).json(updatedUser);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
});

//Delete a User
router.delete('/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const user = await User.findByIdAndDelete(id);

        if(!user) return res.status(404).send("User not found");

        res.status(200).json(user);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
});

//Create a User
router.post('/register', upload.single('profile_pic'), async (req, res) => {
    console.log("Request Body:", req.body);
    console.log("Uploaded File:", req.file);
    try {
        const { first_name, last_name, phone, email, password, role } = req.body;

        // Check if profile picture was uploaded
        let profilePicPath = null;
        if (req.file) {
            profilePicPath = req.file.path; // 👈 Save file path
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Create user
        const user = new User({
            first_name,
            last_name,
            phone,
            email,
            profile_pic: profilePicPath, // 👈 Save file path instead of req.body
            password: hashedPassword,
            role
        });

        await user.save();
        res.status(201).json({ message: "User created successfully", userId: user._id });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: error.message });
    }
});


// User Login
router.post('/login', async (req, res) => {
    try {
        const { phone, password } = req.body;

        // Find user by phone
        const user = await User.findOne({ phone });
        if (!user) return res.status(404).json({ message: 'User not found' });

        // Compare password
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) return res.status(400).json({ message: 'Invalid credentials' });

        // Generate JWT Token
        const token = jwt.sign({ id: user._id, role: user.role }, 'your_secret_key', { expiresIn: '7d' });

        res.json({ token, user });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;