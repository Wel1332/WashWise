import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './features/home/pages/Home';
import Login from './features/auth/pages/Login';
import Register from './features/auth/pages/Register';
import AdminDashboard from './features/dashboard/pages/AdminDashboard';
import StaffDashboard from './features/dashboard/pages/StaffDashboard';
import CustomerDashboard from './features/dashboard/pages/CustomerDashboard';
import UserProfile from './features/profile/pages/UserProfile';
import BookService from './features/order/pages/BookService';
import MyOrders from './features/order/pages/MyOrders';

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