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
import ProtectedRoute from './shared/components/ProtectedRoute';
import NotFound from './shared/components/NotFound';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Customer routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute roles={['CUSTOMER']}>
              <CustomerDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard/customer"
          element={
            <ProtectedRoute roles={['CUSTOMER']}>
              <CustomerDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/book-service"
          element={
            <ProtectedRoute roles={['CUSTOMER']}>
              <BookService />
            </ProtectedRoute>
          }
        />
        <Route
          path="/my-orders"
          element={
            <ProtectedRoute roles={['CUSTOMER']}>
              <MyOrders />
            </ProtectedRoute>
          }
        />

        {/* Staff routes */}
        <Route
          path="/dashboard/staff"
          element={
            <ProtectedRoute roles={['STAFF']}>
              <StaffDashboard />
            </ProtectedRoute>
          }
        />

        {/* Admin routes */}
        <Route
          path="/dashboard/admin"
          element={
            <ProtectedRoute roles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        {/* Shared authenticated routes */}
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <UserProfile />
            </ProtectedRoute>
          }
        />

        {/* Catch-all 404 */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
