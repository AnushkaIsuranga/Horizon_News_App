const express = require('express');
const multer = require('multer');
const cloudinary = require('cloudinary').v2;
const { CloudinaryStorage } = require('multer-storage-cloudinary');
const News = require('../models/news.model');
const { authMiddleware, isEditor, isReporter } = require('../middleware/auth');
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

// Create a news report (Reporter only)
router.post('/create', authMiddleware, isReporter, upload.single('cover_photo'), async (req, res) => {
    try {
        const { title, content, category } = req.body;

        if (!title || !content || !req.file || !category) {
            return res.status(400).json({ message: "All fields are required" });
        }

        // Cloudinary automatically generates a secure URL
        const coverPhotoUrl = req.file.path || req.file.secure_url;

        const report = new News({
            title,
            content,
            cover_photo: coverPhotoUrl,
            category,
            reporter: req.user._id
        });

        await report.save();
        res.status(201).json({ message: "Report created successfully", report });

    } catch (error) {
        console.error("Error creating report:", error);
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

// Get all pending reports (Visible to normal users)
router.get('/pending', async (req, res) => {
    try {
        const reports = await News.find({ status: 'pending' })
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

router.get('/', authMiddleware, async (req, res) => {
    try {
        const { category } = req.query; // Get category from query params
        let filter = {};
        if (category) {
            filter.category = category;
        }

        const newsList = await News.find(filter)
            .populate('reporter', 'first_name last_name')
            .populate('user_comments.user', 'first_name last_name profile_pic');

        res.status(200).json(newsList);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Search reports by title
router.get("/search", async (req, res) => {
    try {
        const { query, category } = req.query;

        // Build the filter
        const filter = { status: "approved" };

        // Add query filter (if provided)
        if (query) {
            filter.$or = [
                { title: { $regex: query, $options: "i" } },
                { content: { $regex: query, $options: "i" } }
            ];
        }

        // Add category filter (if provided and not 'All')
        if (category && category !== "All") {
            filter.category = category;
        }

        // Fetch and sort the results
        const searchResults = await News.find(filter)
            .sort({ createdAt: -1 }) // Sort by most recent first
            .populate("reporter", "first_name last_name"); // Populate reporter details

        res.json({ success: true, reports: searchResults });
    } catch (error) {
        console.error("Search error:", error);
        res.status(500).json({ success: false, message: "Internal Server Error" });
    }
});

// Get a single news article (with comments & ratings)
router.get('/:id', async (req, res) => {
    try {
        const news = await News.findById(req.params.id)
            .populate('reporter', 'first_name last_name')  // Populate reporter fields
            .populate('user_comments.user', 'first_name last_name profile_pic');

        if (!news) return res.status(404).json({ message: "News not found" });

        res.status(200).json({
            id: news._id,
            title: news.title,
            content: news.content,
            category: news.category,
            cover_photo: news.cover_photo,
            status: news.status,
            createdAt: news.createdAt,
            averageRating: news.averageRating,
            reporter: news.reporter
        });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Add user comment & rating
router.post('/:id/user-comment', authMiddleware, async (req, res) => {
    try {
        const { rating, comment } = req.body;

        // Validate input
        if (!rating || !comment) {
            return res.status(400).json({ message: 'Rating and comment are required' });
        }

        if (rating < 1 || rating > 5) {
            return res.status(400).json({ message: 'Rating must be between 1 and 5' });
        }

        // Find the news item
        const news = await News.findById(req.params.id);
        if (!news) return res.status(404).json({ message: 'Report not found' });

        // Create new comment
        const newComment = {
            user: req.user._id,
            rating,
            comment,
            createdAt: new Date()
        };

        // Add comment to the news item
        news.user_comments.push(newComment);
        await news.save();

        // Return success response
        res.json({ message: "Comment added successfully!", comment: newComment });
    } catch (error) {
        console.error("Error adding comment:", error);
        res.status(500).json({ message: error.message });
    }
});

// Fetch comments for a specific news article
router.get('/:id/user-comments', async (req, res) => {
    try {
        const news = await News.findById(req.params.id)
            .populate('user_comments.user', 'first_name last_name profile_pic'); // Populate user details in comments

        if (!news) {
            return res.status(404).json({ message: "News not found" });
        }

        // Extract comments from the news article
        const comments = news.user_comments.map(comment => ({
            id: comment._id,
            user: comment.user,
            rating: comment.rating,
            comment: comment.comment,
            createdAt: comment.createdAt
        }));

        res.status(200).json(comments);
    } catch (error) {
        console.error("Error fetching comments:", error);
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
