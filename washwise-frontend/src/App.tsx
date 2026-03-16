import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';
import StaffDashboard from './pages/StaffDashboard';
import CustomerDashboard from './pages/CustomerDashboard';
import UserProfile from './pages/UserProfile';
import BookService from './pages/BookService';
import MyOrders from './pages/MyOrders';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes - Home has its own navbar */}
        <Route path="/" element={<Home />} />
        
        {/* Auth routes - No navbar */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Dashboard routes - No navbar (they have sidebar) */}
        <Route path="/dashboard" element={<CustomerDashboard />} />
        <Route path="/dashboard/admin" element={<AdminDashboard />} />
        <Route path="/dashboard/staff" element={<StaffDashboard />} />
        <Route path="/dashboard/customer" element={<CustomerDashboard />} />
        <Route path="/book-service" element={<BookService />} />
        <Route path="/my-orders" element={<MyOrders />} /> 

        <Route path="/profile" element={<UserProfile />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;