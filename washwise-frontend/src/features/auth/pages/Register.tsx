import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { User, Mail, Lock, AlertCircle, Loader, Eye, EyeOff, ChevronLeft, Droplets } from 'lucide-react';
import { authAPI } from '../../../shared/services/api';
import { handleApiError } from '../../../shared/utils/errorHandler';
import { useAuthStore } from '../store/authStore';

export default function Register() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [agreeToTerms, setAgreeToTerms] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuthStore();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      setLoading(false);
      return;
    }

    if (password.length < 8) {
      setError('Password must be at least 8 characters');
      setLoading(false);
      return;
    }

    if (!agreeToTerms) {
      setError('You must agree to the Terms of Service and Privacy Policy');
      setLoading(false);
      return;
    }

    try {
      const { data } = await authAPI.register({ email, password, fullName, confirmPassword });
      const { id, email: userEmail, fullName: name, role, accessToken, refreshToken } = data.data;
      login({ id, email: userEmail, fullName: name, role }, accessToken, refreshToken);
      // Route to the correct dashboard based on the role returned by the backend.
      if (role === 'ADMIN') {
        navigate('/dashboard/admin');
      } else if (role === 'STAFF') {
        navigate('/dashboard/staff');
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">
        {/* Back to Homepage Link */}
        <Link 
          to="/" 
          className="inline-flex items-center text-gray-600 hover:text-gray-900 mb-8 text-sm font-medium transition-colors"
        >
          <ChevronLeft size={16} className="mr-1" />
          Back to Homepage
        </Link>

        {/* Register Card */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          {/* Logo and Title */}
          <div className="flex flex-col items-center mb-8">
            <div className="bg-blue-600 p-3 rounded-2xl mb-4">
              <Droplets className="text-white" size={32} strokeWidth={2.5} />
            </div>
            <h1 className="text-2xl font-bold text-gray-900">WashWise</h1>
          </div>

          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold text-gray-900 mb-2">Create Account</h2>
            <p className="text-gray-500 text-sm">Start your journey with professional laundry service</p>
          </div>
          
          {/* Error Message */}
          {error && (
            <div className="bg-red-50 text-red-700 p-4 rounded-xl mb-6 flex items-start gap-3 border border-red-200">
              <AlertCircle size={20} className="flex-shrink-0 mt-0.5" />
              <span className="text-sm">{error}</span>
            </div>
          )}
          
          {/* Register Form */}
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Full Name Field */}
            <div>
              <label className="block text-gray-900 mb-2 font-medium text-sm flex items-center gap-2">
                <User size={16} className="text-blue-600" />
                Full Name
              </label>
              <input
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900 placeholder-gray-400 transition-all"
                placeholder="Enter your full name"
                required
              />
            </div>

            {/* Email Field */}
            <div>
              <label className="block text-gray-900 mb-2 font-medium text-sm flex items-center gap-2">
                <Mail size={16} className="text-blue-600" />
                Email Address
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900 placeholder-gray-400 transition-all"
                placeholder="Enter your email"
                required
              />
            </div>
            
            {/* Password Field */}
            <div>
              <label className="block text-gray-900 mb-2 font-medium text-sm flex items-center gap-2">
                <Lock size={16} className="text-blue-600" />
                Password
              </label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900 placeholder-gray-400 transition-all"
                  placeholder="Create a password"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 transition-colors"
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>
            
            {/* Confirm Password Field */}
            <div>
              <label className="block text-gray-900 mb-2 font-medium text-sm flex items-center gap-2">
                <Lock size={16} className="text-blue-600" />
                Confirm Password
              </label>
              <div className="relative">
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900 placeholder-gray-400 transition-all"
                  placeholder="Confirm your password"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 transition-colors"
                >
                  {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            {/* Terms & Privacy */}
            <div className="flex items-start">
              <input
                type="checkbox"
                checked={agreeToTerms}
                onChange={(e) => setAgreeToTerms(e.target.checked)}
                className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-2 focus:ring-blue-600 cursor-pointer mt-1"
                required
              />
              <label className="ml-3 text-sm text-gray-600">
                I agree to the
                <Link to="/terms" className="text-blue-600 hover:text-blue-700 font-medium mx-1">
                  Terms of Service
                </Link>
                and
                <Link to="/privacy" className="text-blue-600 hover:text-blue-700 font-medium ml-1">
                  Privacy Policy
                </Link>
              </label>
            </div>
            
            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3.5 rounded-xl font-semibold disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 transition-all shadow-sm hover:shadow-md"
            >
              {loading ? (
                <>
                  <Loader size={20} className="animate-spin" />
                  Creating account...
                </>
              ) : (
                'Create Account'
              )}
            </button>
          </form>
          
          {/* Sign In Link */}
          <p className="text-center mt-6 text-sm text-gray-600">
            Already have an account?
            <Link 
              to="/login" 
              className="text-blue-600 hover:text-blue-700 font-semibold ml-1 transition-colors"
            >
              Sign In
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}