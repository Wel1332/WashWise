import { Link } from 'react-router-dom';
import { LogOut, LogIn, UserPlus, Menu, X, Shirt, LayoutDashboard, BarChart3, User } from 'lucide-react';
import { useState } from 'react';
import { useAuthStore } from '../store/authStore';

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuthStore();
  const [isOpen, setIsOpen] = useState(false);

  return (
    <nav className="bg-white shadow-sm border-b border-gray-100">
      <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <Link to="/" className="text-2xl font-bold text-blue-600 flex items-center gap-2">
          <Shirt className="text-blue-600" size={32} />
          <span className="text-gray-900">WashWise</span>
        </Link>
        
        <div className="hidden md:flex gap-8 items-center">
          <Link to="/services" className="text-gray-700 hover:text-blue-600 font-medium transition">
            Services
          </Link>
          
          {isAuthenticated && (
            <>
              {/* Dashboard link for authenticated users */}
              <Link to="/dashboard" className="text-gray-700 hover:text-blue-600 font-medium transition flex items-center gap-2">
                <LayoutDashboard size={18} />
                Dashboard
              </Link>

              {/* Profile link for authenticated users */}
              <Link to="/profile" className="text-gray-700 hover:text-blue-600 font-medium transition flex items-center gap-2">
                <User size={18} />
                Profile
              </Link>

              {/* My Orders link for customers */}
              {user?.role === 'CUSTOMER' && (
                <Link to="/my-orders" className="text-gray-700 hover:text-blue-600 font-medium transition">
                  My Orders
                </Link>
              )}

              {/* Admin/Staff manage & analytics links */}
              {(user?.role === 'ADMIN' || user?.role === 'STAFF') && (
                <>
                  <Link to="/admin" className="text-gray-700 hover:text-blue-600 font-medium transition">
                    Manage Orders
                  </Link>
                  {user?.role === 'ADMIN' && (
                    <Link to="/analytics" className="text-gray-700 hover:text-blue-600 font-medium transition flex items-center gap-2">
                      <BarChart3 size={18} />
                      Analytics
                    </Link>
                  )}
                </>
              )}
            </>
          )}
          
          {isAuthenticated ? (
            <div className="flex gap-4 items-center border-l border-gray-200 pl-8">
              <div className="flex flex-col">
                <span className="text-gray-900 font-medium">{user?.fullName}</span>
                <span className="text-xs text-gray-500 capitalize">{user?.role}</span>
              </div>
              <button
                onClick={logout}
                className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition"
              >
                <LogOut size={18} />
                Logout
              </button>
            </div>
          ) : (
            <div className="flex gap-3">
              <Link
                to="/login"
                className="border-2 border-blue-600 text-blue-600 hover:bg-blue-50 px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition"
              >
                <LogIn size={18} />
                Login
              </Link>
              <Link
                to="/register"
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition"
              >
                <UserPlus size={18} />
                Register
              </Link>
            </div>
          )}
        </div>

        <button
          className="md:hidden text-gray-900"
          onClick={() => setIsOpen(!isOpen)}
        >
          {isOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* MOBILE MENU */}
      {isOpen && (
        <div className="md:hidden bg-gray-50 border-t border-gray-100 p-4 space-y-3">
          <Link
            to="/services"
            className="block text-gray-700 hover:text-blue-600 font-medium py-2 px-4 rounded hover:bg-gray-100"
          >
            Services
          </Link>
          
          {isAuthenticated && (
            <>
              {/* Dashboard link for mobile */}
              <Link
                to="/dashboard"
                className="block text-gray-700 hover:text-blue-600 font-medium py-2 px-4 rounded hover:bg-gray-100 flex items-center gap-2"
              >
                <LayoutDashboard size={18} />
                Dashboard
              </Link>

              {/* Profile link for mobile */}
              <Link
                to="/profile"
                className="block text-gray-700 hover:text-blue-600 font-medium py-2 px-4 rounded hover:bg-gray-100 flex items-center gap-2"
              >
                <User size={18} />
                Profile
              </Link>

              {/* My Orders link for customers */}
              {user?.role === 'CUSTOMER' && (
                <Link
                  to="/my-orders"
                  className="block text-gray-700 hover:text-blue-600 font-medium py-2 px-4 rounded hover:bg-gray-100"
                >
                  My Orders
                </Link>
              )}

              {/* Admin/Staff manage & analytics links for mobile */}
              {(user?.role === 'ADMIN' || user?.role === 'STAFF') && (
                <>
                  <Link
                    to="/admin"
                    className="block text-gray-700 hover:text-blue-600 font-medium py-2 px-4 rounded hover:bg-gray-100"
                  >
                    Manage Orders
                  </Link>
                  {user?.role === 'ADMIN' && (
                    <Link
                      to="/analytics"
                      className="block text-gray-700 hover:text-blue-600 font-medium py-2 px-4 rounded hover:bg-gray-100 flex items-center gap-2"
                    >
                      <BarChart3 size={18} />
                      Analytics
                    </Link>
                  )}
                </>
              )}

              {/* User info in mobile menu */}
              <div className="px-4 py-2 bg-blue-50 rounded border border-blue-200 mt-4">
                <p className="text-gray-900 font-semibold">{user?.fullName}</p>
                <p className="text-xs text-gray-600 capitalize">Role: {user?.role}</p>
              </div>
            </>
          )}
          
          {isAuthenticated ? (
            <button
              onClick={() => { logout(); setIsOpen(false); }}
              className="w-full bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium flex items-center justify-center gap-2 mt-4"
            >
              <LogOut size={18} />
              Logout
            </button>
          ) : (
            <div className="space-y-2">
              <Link
                to="/login"
                className="w-full block border-2 border-blue-600 text-blue-600 hover:bg-blue-50 px-4 py-2 rounded-lg font-medium text-center transition"
              >
                <LogIn className="inline mr-2" size={18} />
                Login
              </Link>
              <Link
                to="/register"
                className="w-full block bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium text-center"
              >
                <UserPlus className="inline mr-2" size={18} />
                Register
              </Link>
            </div>
          )}
        </div>
      )}
    </nav>
  );
}