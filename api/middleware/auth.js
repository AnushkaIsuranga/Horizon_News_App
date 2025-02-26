const jwt = require('jsonwebtoken');
const User = require('../models/user.model');

const authMiddleware = async (req, res, next) => {
    try {
        const token = req.header('Authorization').replace('Bearer ', '');
        const decoded = jwt.verify(token, 'your_secret_key');
        const user = await User.findById(decoded.id);

        if (!user) throw new Error();
        req.user = user;
        next();
    } catch (error) {
        res.status(401).json({ message: 'Unauthorized' });
    }
};

const isEditor = (req, res, next) => {
    if (req.user.role !== 'editor') {
        return res.status(403).json({ message: 'Access denied' });
    }
    next();
};

const isReporter = (req, res, next) => {
    if (req.user.role !== 'reporter') {
        return res.status(403).json({ message: 'Access denied' });
    }
    next();
};

module.exports = { authMiddleware, isEditor, isReporter };
