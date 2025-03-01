const mongoose = require('mongoose');

const EditorSchema = new mongoose.Schema({
    first_name: String,
    last_name: String,
    email: { type: String, unique: true, required: true },
    password: { type: String, required: true }
});

const Editor = mongoose.model('Editor', adminSchema);

module.exports = Editor;
