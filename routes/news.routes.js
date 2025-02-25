const express = require('express');
const News = require('../models/news.model');
const router = express.Router();

//Create a News Article
router.post('/', async (req, res) => {
    try {
        const news = new News(req.body);
        await news.save();
        res.status(201).json(news);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

//Get All News Articles
router.get('/', async (req, res) => {
    try {
        const newsList = await News.find().populate('comments.user', 'first_name last_name profile_pic').populate('ratings.user', 'first_name last_name');
        res.status(200).json(newsList);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

//Get a Single News Article (with comments & ratings)
router.get('/:id', async (req, res) => {
    try {
        const news = await News.findById(req.params.id)
            .populate('comments.user', 'first_name last_name profile_pic')
            .populate('ratings.user', 'first_name last_name');

        if (!news) return res.status(404).json({ message: "News not found" });

        res.status(200).json({
            ...news._doc,
            averageRating: news.averageRating
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

//Add a Comment to a News Article
router.post('/:id/comments', async (req, res) => {
    try {
        const { user, text } = req.body;
        const news = await News.findById(req.params.id);

        if (!news) return res.status(404).json({ message: "News not found" });

        news.comments.push({ user, text });
        await news.save();

        res.status(201).json(news);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

//Add a Rating to a News Article
router.post('/:id/ratings', async (req, res) => {
    try {
        const { user, rating } = req.body;
        const news = await News.findById(req.params.id);

        if (!news) return res.status(404).json({ message: "News not found" });

        news.ratings.push({ user, rating });
        await news.save();

        res.status(201).json({ message: "Rating added", averageRating: news.averageRating });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

//Delete a News Article
router.delete('/:id', async (req, res) => {
    try {
        const news = await News.findByIdAndDelete(req.params.id);
        if (!news) return res.status(404).json({ message: "News not found" });

        res.status(200).json({ message: "News deleted successfully" });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;
