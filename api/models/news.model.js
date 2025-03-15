const mongoose = require('mongoose');

const newsSchema = new mongoose.Schema({
    title: { 
        type: String, 
        required: true 
    },
    content: { 
        type: String, 
        required: true 
    },
    category: {
        type: String,
        enum: ['Sports', 'Technology', 'Politics', 'Gossip', 'Weather', 'Business', 'Entertainment', 'Lifestyle'],
        required: true
    },

    cover_photo: { 
        type: String, 
        required: true 
    },
    reporter: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'User', 
        required: true 
    },
    status: { 
        type: String, 
        enum: ['pending', 'approved', 'rejected'], 
        default: 'pending' 
    },
    recommendation_level: { 
        type: Number, 
        default: 0 
    },
    editor_ratings: [
        {
            editor: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
            rating: { type: Number, min: 1, max: 5 },
            comment: String
        }
    ],
    user_comments: [
        {
            user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
            rating: { type: Number, min: 1, max: 5 },
            comment: String
        }
    ]
}, { timestamps: true, toJSON: { virtuals: true }, toObject: { virtuals: true } });

// Virtual field to calculate average rating from user comments
newsSchema.virtual('averageRating').get(function () {
    if (this.user_comments.length === 0) return 0;
    const total = this.user_comments.reduce((sum, r) => sum + r.rating, 0);
    return (total / this.user_comments.length).toFixed(1);
});

const News = mongoose.model('News', newsSchema);
module.exports = News;
