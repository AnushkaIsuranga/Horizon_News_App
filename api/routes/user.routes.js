const express = require('express');
const multer = require('multer');
const cloudinary = require('cloudinary').v2;
const { CloudinaryStorage } = require('multer-storage-cloudinary');
const User = require('../models/user.model');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const router = express.Router();
const app = express();

//Configure Cloudinary
cloudinary.config({
    cloud_name: 'dkpre5kis',
    api_key: '832397485563889',
    api_secret: '5o9oI98AvyvnMoKBa6vlEa0xqZY'
});

//Set Up Multer Storage for Cloudinary
const storage = new CloudinaryStorage({
    cloudinary: cloudinary,
    params: {
        folder: 'profile_pics', // Change folder name as needed
        allowed_formats: ['jpg', 'png', 'jpeg']
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

// Create a User
router.post('/register', upload.single('profile_pic'), async (req, res) => {
    try {
        console.log("Received Request Body:", req.body);
        console.log("Received File:", req.file);

        const { first_name, last_name, phone, email, password, role } = req.body;

        if (!first_name || !last_name || !phone || !email || !password) {
            return res.status(400).json({ message: "All fields are required" });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);
        console.log(`Original Password: ${password}`);
        console.log(`Hashed Password: ${hashedPassword}`);

        const profilePicUrl = req.file ? req.file.path : null;

        const user = new User({
            first_name,
            last_name,
            phone,
            email,
            password: hashedPassword,  // Ensure hashed password is stored
            role,
            profile_pic: profilePicUrl
        });

        await user.save();
        res.status(201).json({ message: "User created successfully", userId: user._id, profile_pic: profilePicUrl });

    } catch (error) {
        console.error("Error in Registration:", error);
        res.status(500).json({ message: error.message });
    }
});

// User Login
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        console.log(`Login attempt for email: ${email}`);

        const user = await User.findOne({ email }).select("+password");

        if (!user) {
            console.log("User not found in DB");
            return res.status(404).json({ message: "User not found" });
        }

        console.log(`Stored hashed password: ${user.password}`);
        console.log(`Entered password: ${req.body.password}`);

        const isMatch = await bcrypt.compare(req.body.password, user.password);
        console.log(`Password comparison result: ${isMatch}`);

        if (!isMatch) {
            console.log("Invalid credentials");
            return res.status(400).json({ message: "Invalid credentials" });
        }

        const token = jwt.sign({ id: user._id, role: user.role }, 'your_secret_key', { expiresIn: '7d' });

        console.log("Login successful");
        res.json({ token, user });
    } catch (error) {
        console.error("Error:", error);
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;