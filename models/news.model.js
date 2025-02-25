const mongoose = require('mongoose');

const newsSchema = new mongoose.Schema({
    title: {
        type: String,
        required: true
    },
    cover_photo: {
        type: String,
        required: true
    },
    content: {
        type: String,
        required: true
    },
    comments: [
        {
            user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }, // Reference to User
            text: String,
            createdAt: { type: Date, default: Date.now }
        }
    ],
    ratings: [
        {
            user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }, // Reference to User
            rating: { type: Number, min: 1, max: 5 }
        }
    ]
}, { timestamps: true });

// Virtual field to calculate average rating
newsSchema.virtual('averageRating').get(function () {
    if (this.ratings.length === 0) return 0;
    const total = this.ratings.reduce((sum, r) => sum + r.rating, 0);
    return (total / this.ratings.length).toFixed(1);
});

const News = mongoose.model('News', newsSchema);

module.exports = News;