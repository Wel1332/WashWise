import { Link, useLocation } from 'react-router-dom';
import { Droplets, Menu, X, LayoutDashboard, User, BarChart3, LogOut } from 'lucide-react';
import { useState } from 'react';
import { useAuthStore } from '../store/authStore';

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuthStore();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const location = useLocation();

  const hiddenRoutes = ['/dashboard', '/login', '/register'];
  const shouldHideNavbar = hiddenRoutes.some(route => location.pathname.startsWith(route));

  if (shouldHideNavbar) {
    return null;
  }

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2" onClick={closeMenu}>
            <div className="bg-blue-600 p-2 rounded-xl">
              <Droplets className="text-white" size={24} />
            </div>
            <span className="text-xl font-bold text-gray-900">WashWise</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center gap-8">
            <Link to="/services" className="text-gray-600 hover:text-gray-900 font-medium transition-colors">
              Services
            </Link>
            <a href="/#about" className="text-gray-600 hover:text-gray-900 font-medium transition-colors">
              About
            </a>
            <a href="/#contact" className="text-gray-600 hover:text-gray-900 font-medium transition-colors">
              Contact
            </a>

            {/* Authenticated Links */}
            {isAuthenticated && (
              <>
                <Link to="/dashboard" className="text-gray-600 hover:text-gray-900 font-medium transition-colors flex items-center gap-2">
                  <LayoutDashboard size={18} />
                  Dashboard
                </Link>
                <Link to="/profile" className="text-gray-600 hover:text-gray-900 font-medium transition-colors flex items-center gap-2">
                  <User size={18} />
                  Profile
                </Link>

                {user?.role === 'CUSTOMER' && (
                  <Link to="/my-orders" className="text-gray-600 hover:text-gray-900 font-medium transition-colors">
                    My Orders
                  </Link>
                )}

                {(user?.role === 'ADMIN' || user?.role === 'STAFF') && (
                  <>
                    <Link to="/admin" className="text-gray-600 hover:text-gray-900 font-medium transition-colors">
                      Manage Orders
                    </Link>
                    {user?.role === 'ADMIN' && (
                      <Link to="/analytics" className="text-gray-600 hover:text-gray-900 font-medium transition-colors flex items-center gap-2">
                        <BarChart3 size={18} />
                        Analytics
                      </Link>
                    )}
                  </>
                )}
              </>
            )}

            {/* User State / Auth Buttons */}
            {isAuthenticated ? (
              <div className="flex gap-4 items-center border-l border-gray-200 pl-8">
                <div className="flex flex-col">
                  <span className="text-gray-900 font-medium text-sm">{user?.fullName}</span>
                  <span className="text-xs text-gray-500 capitalize">{user?.role}</span>
                </div>
                <button
                  onClick={logout}
                  className="text-gray-500 hover:text-red-600 p-2 rounded-lg hover:bg-red-50 transition-colors flex items-center gap-2"
                  title="Logout"
                >
                  <LogOut size={20} />
                </button>
              </div>
            ) : (
              <>
                <Link 
                  to="/login" 
                  className="text-gray-600 hover:text-gray-900 font-medium transition-colors"
                >
                  Login
                </Link>
                <Link 
                  to="/register" 
                  className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-lg font-semibold transition-all"
                >
                  Get Started
                </Link>
              </>
            )}
          </div>

          {/* Mobile Menu Button */}
          <button 
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 rounded-lg hover:bg-gray-100 text-gray-600"
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-gray-200 bg-white">
          <div className="px-4 py-4 space-y-3">
            <Link to="/services" onClick={closeMenu} className="block text-gray-600 hover:text-gray-900 font-medium py-2">
              Services
            </Link>
            <a href="/#about" onClick={closeMenu} className="block text-gray-600 hover:text-gray-900 font-medium py-2">
              About
            </a>
            <a href="/#contact" onClick={closeMenu} className="block text-gray-600 hover:text-gray-900 font-medium py-2">
              Contact
            </a>

            {isAuthenticated && (
              <>
                <Link to="/dashboard" onClick={closeMenu} className="flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium py-2">
                  <LayoutDashboard size={18} /> Dashboard
                </Link>
                <Link to="/profile" onClick={closeMenu} className="flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium py-2">
                  <User size={18} /> Profile
                </Link>

                {user?.role === 'CUSTOMER' && (
                  <Link to="/my-orders" onClick={closeMenu} className="block text-gray-600 hover:text-gray-900 font-medium py-2">
                    My Orders
                  </Link>
                )}

                {(user?.role === 'ADMIN' || user?.role === 'STAFF') && (
                  <>
                    <Link to="/admin" onClick={closeMenu} className="block text-gray-600 hover:text-gray-900 font-medium py-2">
                      Manage Orders
                    </Link>
                    {user?.role === 'ADMIN' && (
                      <Link to="/analytics" onClick={closeMenu} className="flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium py-2">
                        <BarChart3 size={18} /> Analytics
                      </Link>
                    )}
                  </>
                )}
                
                <div className="px-4 py-3 bg-blue-50 rounded-xl border border-blue-100 mt-4 mb-2">
                  <p className="text-gray-900 font-semibold">{user?.fullName}</p>
                  <p className="text-xs text-gray-600 capitalize">Role: {user?.role}</p>
                </div>
              </>
            )}

            {isAuthenticated ? (
               <button
                 onClick={() => { logout(); closeMenu(); }}
                 className="w-full bg-red-50 text-red-600 hover:bg-red-100 px-6 py-2.5 rounded-lg font-semibold flex items-center justify-center gap-2 transition-all mt-4"
               >
                 <LogOut size={18} /> Logout
               </button>
            ) : (
              <>
                <Link to="/login" onClick={closeMenu} className="block text-gray-600 hover:text-gray-900 font-medium py-2">
                  Login
                </Link>
                <Link 
                  to="/register" 
                  onClick={closeMenu}
                  className="block bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-lg font-semibold text-center mt-2"
                >
                  Get Started
                </Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  );
}