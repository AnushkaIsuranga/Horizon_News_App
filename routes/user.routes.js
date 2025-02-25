const express = require('express');
const User = require('../models/user.model');
const router = express.Router();
const app = express();

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
router.post('/', async (req, res) => {
    try {
        const user = new User(req.body);
        await user.save();
        res.send(user);
    } catch (error) {
        res.status(500).send(error);
    }
});

module.exports = router;