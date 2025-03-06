import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Signin from './Signin.jsx';
import Home from './home.jsx';
import AddUser from './AddUser.jsx';
import PrivateRoute from './PrivateRoute.jsx';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Signin />} />
        
        {/* Protected Routes */}
        <Route element={<PrivateRoute />}>
          <Route path="/home" element={<Home />} />
          <Route path="/add-user" element={<AddUser />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
