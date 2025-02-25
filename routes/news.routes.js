const express = require('express');
const News = require('../models/news.model');
const { authMiddleware, isEditor, isReporter } = require('../middleware/auth');
const router = express.Router();

// Create a news report (Reporter only)
router.post('/', authMiddleware, isReporter, async (req, res) => {
    try {
        const { title, content, cover_photo } = req.body;
        const news = new News({
            title,
            content,
            cover_photo,
            reporter: req.user._id
        });

        await news.save();
        res.status(201).json(news);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Get all news articles (for admins & editors)
router.get('/', authMiddleware, async (req, res) => {
    try {
        const newsList = await News.find()
            .populate('reporter', 'first_name last_name')
            .populate('user_comments.user', 'first_name last_name profile_pic');

        res.status(200).json(newsList);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Get all approved reports (Visible to normal users)
router.get('/approved', async (req, res) => {
    try {
        const reports = await News.find({ status: 'approved' })
            .sort({ recommendation_level: -1 }) // Sort by editor rating
            .populate('reporter', 'first_name last_name')
            .populate('user_comments.user', 'first_name last_name')
            .lean(); // Convert to plain JS object

        res.json(reports.map(report => ({
            ...report,
            averageRating: report.averageRating // Explicitly include virtual field
        })));
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Get a single news article (with comments & ratings)
router.get('/:id', async (req, res) => {
    try {
        const news = await News.findById(req.params.id)
            .populate('reporter', 'first_name last_name')
            .populate('user_comments.user', 'first_name last_name profile_pic');

        if (!news) return res.status(404).json({ message: "News not found" });

        res.status(200).json({
            ...news._doc,
            averageRating: news.averageRating
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Add user comment & rating (Visible to everyone)
router.post('/:id/user-comment', authMiddleware, async (req, res) => {
    try {
        const { rating, comment } = req.body;
        if (rating < 1 || rating > 5) {
            return res.status(400).json({ message: 'Rating must be between 1 and 5' });
        }

        const news = await News.findById(req.params.id);
        if (!news) return res.status(404).json({ message: 'Report not found' });

        news.user_comments.push({ user: req.user._id, rating, comment });

        await news.save();
        res.json(news);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Delete a news article
router.delete('/:id', authMiddleware, async (req, res) => {
    try {
        const news = await News.findByIdAndDelete(req.params.id);
        if (!news) return res.status(404).json({ message: "News not found" });

        res.status(200).json({ message: "News deleted successfully" });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Approve or Reject report (Editor only)
router.put('/:id/status', authMiddleware, isEditor, async (req, res) => {
    try {
        const { status } = req.body;
        if (!['approved', 'rejected'].includes(status)) {
            return res.status(400).json({ message: 'Invalid status' });
        }

        const news = await News.findByIdAndUpdate(req.params.id, { status }, { new: true });

        if (!news) return res.status(404).json({ message: 'Report not found' });

        res.json(news);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Add editor rating & comment (Editor only)
router.post('/:id/editor-rating', authMiddleware, isEditor, async (req, res) => {
    try {
        const { rating, comment } = req.body;
        if (rating < 1 || rating > 5) {
            return res.status(400).json({ message: 'Rating must be between 1 and 5' });
        }

        const news = await News.findById(req.params.id);
        if (!news) return res.status(404).json({ message: 'Report not found' });

        news.editor_ratings.push({ editor: req.user._id, rating, comment });

        // Calculate recommendation level (average of all editor ratings)
        const totalRating = news.editor_ratings.reduce((sum, r) => sum + r.rating, 0);
        news.recommendation_level = totalRating / news.editor_ratings.length;

        await news.save();
        res.json(news);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Fetch reports by editor or reporter (Authenticated users only)
router.get('/user-reports', authMiddleware, async (req, res) => {
    try {
        let query = { reporter: req.user._id };
        if (req.user.role === 'editor') {
            query = {}; // Editors can see all reports
        }

        const reports = await News.find(query)
            .populate('reporter', 'first_name last_name')
            .populate('user_comments.user', 'first_name last_name');

        res.json(reports);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;
