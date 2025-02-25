const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const userRoutes = require('./routes/user.routes');
const newsRoutes = require('./routes/news.routes')

const app = express();

// Middleware
app.use(cors());
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// Routes
app.use('/api/users', userRoutes);
app.use('/api/news', newsRoutes);

app.get('/', (req, res) => {
    res.send("Hello Node");
});

// Database Connection
mongoose.connect("mongodb+srv://isuranga880:c2OnLlTo4LENitSg@cluster0.ofcf3.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")
.then(() => {
    console.log("Connected to MongoDB");
    app.listen(3000, () => {
        console.log("Server is runing on port 3000");
    });
})
.catch((err) => {
    console.log("Error: ", err);
});
mongoose.connection.on('error', (err) => console.error("MongoDB Error:", err));
