import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Services from './pages/Services';
import ServiceDetail from './pages/ServiceDetail';
import MyOrders from './pages/MyOrders';
import AdminDashboard from './pages/AdminDashboard';
import CustomerDashboard from './pages/CustomerDashboard';
import StaffDashboard from './pages/StaffDashboard';
import ProtectedRoute from './components/ProtectedRoute';
import AnalyticsDashboard from './pages/AnalyticsDashboard';
import UserProfile from './pages/UserProfile';

function App() {
  const { isAuthenticated, user } = useAuthStore();

  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <Routes>
          {/* PUBLIC ROUTES */}
          <Route path="/" element={<Home />} />
          <Route path="/services" element={<Services />} />
          <Route path="/services/:id" element={<ServiceDetail />} />
          
          {/* AUTH ROUTES */}
          <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
          <Route path="/register" element={!isAuthenticated ? <Register /> : <Navigate to="/dashboard" />} />
          
          {/* PROTECTED ROUTES */}
          <Route
            path="/my-orders"
            element={<ProtectedRoute><MyOrders /></ProtectedRoute>}
          />
          
          {/* DASHBOARD ROUTES - Role Based */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                {user?.role === 'ADMIN' ? (
                  <AdminDashboard />
                ) : user?.role === 'STAFF' ? (
                  <StaffDashboard />
                ) : (
                  <CustomerDashboard />
                )}
              </ProtectedRoute>
            }
          />
          
          {/* ADMIN ONLY ROUTE */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                {user?.role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/dashboard" />}
              </ProtectedRoute>
            }
          />
          
          {/* STAFF ONLY ROUTE */}
          <Route
            path="/staff"
            element={
              <ProtectedRoute>
                {user?.role === 'STAFF' ? <StaffDashboard /> : <Navigate to="/dashboard" />}
              </ProtectedRoute>
            }
          />
          
          {/* CATCH ALL - Redirect to home */}
          <Route path="*" element={<Navigate to="/" />} />
          <Route
            path="/analytics"
            element={
              <ProtectedRoute>
                {user?.role === 'ADMIN' ? <AnalyticsDashboard /> : <Navigate to="/dashboard" />}
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={<ProtectedRoute><UserProfile /></ProtectedRoute>}
          />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;