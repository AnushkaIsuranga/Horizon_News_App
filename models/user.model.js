const mongoose = require('mongoose');

const useSchema = new mongoose.Schema({
    first_name: {
        type: String,
        required: [true, 'First name is required']
    },
    last_name: {
        type: String,
        required: [true, 'Last name is required']
    },
    phone: {
        type: Number,
        required: [true, 'Phone number is required']
    },
    profile_pic: {
        type: String,
        required: false
    },
    password: {
        type: String,
        required: [true, 'Password is required']
    },
    role: {
        type: String,
        required: [true, 'Role is required'],
        default: 'user'
    }
}, {
    timestamps: true
});

const User = mongoose.model('User', useSchema);

module.exports = User;