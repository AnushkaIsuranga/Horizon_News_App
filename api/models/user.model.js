const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({  // ✅ Change "useSchema" to "userSchema"
    first_name: {
        type: String,
        required: [true, 'First name is required']
    },
    last_name: {
        type: String,
        required: [true, 'Last name is required']
    },
    phone: {
        type: String,
        required: [true, 'Phone number is required']
    },
    email: {
        type: String,
        required: [true, 'Email is required'],
        unique: true
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
        enum: ['user', 'editor', 'reporter'],
        default: 'user'
    }
}, {
    timestamps: true
});

userSchema.pre('save', async function (next) {
    if (!this.isModified('password')) return next();
    console.log("Hashing password:", this.password);
    this.password = await bcrypt.hash(this.password, 10);
    console.log("Hashed password:", this.password);
    next();
});

const User = mongoose.model('User', userSchema);

module.exports = User;
