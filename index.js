const express = require('express');
const { default: mongoose } = require('mongoose');
const userRoutes = require('./routes/user.routes');
const app = express()

//Middleware
app.use(express.urlencoded({extended: true}));
app.use(express.json());

//Routes
app.use('/api/users', userRoutes);

app.get('/', (req, res) => {
    res.send("Hello Node")
});

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