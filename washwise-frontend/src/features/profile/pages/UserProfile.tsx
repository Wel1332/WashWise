import { useState, useEffect } from "react";
import { 
  Camera, 
  Eye, 
  EyeOff, 
  Lock, 
  Mail, 
  User as UserIcon, 
  LogOut,
  LayoutDashboard,
  ShoppingCart,
  Package,
  UserCircle,
  AlertTriangle,
  Shield // <-- Added Shield icon for the privacy modal
} from "lucide-react";
import { useAuthStore } from '../../../features/auth/store/authStore';
import { useNavigate } from 'react-router-dom';
import { Droplets } from 'lucide-react';
import { profileAPI } from '../../../shared/services/api';

interface Profile {
  id: string;
  fullName: string;
  email: string;
  role: string;
  bio?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  zipCode?: string;
  profileImageBase64?: string;
}

export default function UserProfile() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [profileImage, setProfileImage] = useState<string | null>(null);
  const [imageLoading, setImageLoading] = useState(false);
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showPrivacyModal, setShowPrivacyModal] = useState(false); // <-- Added state for Privacy Modal
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phoneNumber: "",
    address: "",
    city: "",
    zipCode: "",
    bio: "",
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const { data } = await profileAPI.getProfile();
      const profileData = data.data;
      setProfile(profileData);
      setFormData({
        fullName: profileData.fullName || "",
        email: profileData.email || "",
        phoneNumber: profileData.phoneNumber || "",
        address: profileData.address || "",
        city: profileData.city || "",
        zipCode: profileData.zipCode || "",
        bio: profileData.bio || "",
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      
      if (profileData.profileImageBase64) {
        setProfileImage(profileData.profileImageBase64);
      }
    } catch (err: any) {
      console.error('Error fetching profile:', err);
      setProfile({
        id: user?.id || '',
        fullName: user?.fullName || '',
        email: user?.email || '',
        role: user?.role || 'CUSTOMER',
      });
    }
  };

  const handleImageChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setImageLoading(true);
    setError('');
    setSuccess('');

    try {
      const { data } = await profileAPI.uploadProfileImage(file);
      setProfile(data.data);
      if (data.data.profileImageBase64) {
        setProfileImage(data.data.profileImageBase64);
      }
      setSuccess('Profile image updated successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError('Failed to upload image');
    } finally {
      setImageLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSaveProfile = async () => {
    setError('');
    setSuccess('');

    try {
      const updateData = {
        phoneNumber: formData.phoneNumber,
        address: formData.address,
        city: formData.city,
        zipCode: formData.zipCode,
        bio: formData.bio,
      };

      const { data } = await profileAPI.updateProfile(updateData);
      setProfile(data.data);
      setSuccess('Profile information saved successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile');
    }
  };

  const handleChangePassword = async () => {
    if (formData.newPassword !== formData.confirmPassword) {
      setError('New passwords do not match!');
      return;
    }
    if (formData.newPassword.length < 8) { 
      setError('Password must be at least 8 characters long!');
      return;
    }
    
    try {
      await profileAPI.changePassword({
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword
      });
      
      setSuccess('Password changed successfully!');
      setFormData({
        ...formData,
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to change password');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Account Action Handlers
  const handleDownloadData = () => {
    if (!profile) return;
    const dataStr = JSON.stringify(profile, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `washwise_data_${profile.fullName.replace(/\s+/g, '_')}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    
    setSuccess('Your data has been downloaded successfully.');
    setTimeout(() => setSuccess(''), 3000);
  };

  // Updated to show custom modal instead of browser alert
  const handlePrivacySettings = () => {
    setShowPrivacyModal(true);
  };

  const handleDeleteClick = () => {
    setShowDeleteConfirm(true);
  };

  const confirmDeleteAccount = async () => {
    try {
      await profileAPI.deleteAccount();
      logout();
      navigate('/register');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete account. You may have active orders.');
      setShowDeleteConfirm(false);
    }
  };

  const displayProfile = profile || {
    id: user?.id || '',
    fullName: user?.fullName || '',
    email: user?.email || '',
    role: user?.role || 'CUSTOMER',
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col flex-shrink-0">
        <div className="p-6 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <div className="bg-blue-600 p-2 rounded-xl">
              <Droplets className="text-white" size={24} />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-900">WashWise</h1>
              <p className="text-xs text-gray-500">Customer Portal</p>
            </div>
          </div>
        </div>

        <nav className="flex-1 p-4">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">
            MENU
          </p>
          <div className="space-y-1">
            <button
              onClick={() => navigate('/dashboard')}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-gray-700 hover:bg-gray-100 transition-all"
            >
              <LayoutDashboard size={20} />
              <span>Dashboard</span>
            </button>

            <button
              onClick={() => navigate('/book-service')}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-gray-700 hover:bg-gray-100 transition-all"
            >
              <ShoppingCart size={20} />
              <span>Book Service</span>
            </button>

            <button
              onClick={() => navigate('/my-orders')}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-gray-700 hover:bg-gray-100 transition-all"
            >
              <Package size={20} />
              <span>My Orders</span>
            </button>

            <button
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium bg-blue-600 text-white transition-all"
            >
              <UserCircle size={20} />
              <span>Profile</span>
            </button>
          </div>
        </nav>

        <div className="p-4 border-t border-gray-200">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center overflow-hidden">
              {profileImage ? (
                <img 
                  src={profileImage} 
                  alt="Profile" 
                  className="w-full h-full object-cover" 
                  onError={() => setProfileImage(null)} 
                />
              ) : (
                <UserIcon className="text-white" size={24} />
              )}
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-semibold text-gray-900 text-sm truncate">{displayProfile.fullName}</p>
              <p className="text-xs text-gray-500 truncate">{displayProfile.email}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 text-red-600 hover:bg-red-50 px-4 py-2 rounded-lg font-medium transition-all"
          >
            <LogOut size={18} />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-auto relative">
        <div className="p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Profile Settings</h1>
            <p className="text-lg text-gray-600">Manage your account information and security</p>
          </div>

          {/* Success/Error Messages */}
          {success && (
            <div className="bg-green-50 text-green-700 p-4 rounded-xl mb-6 flex items-center gap-3 border border-green-200">
              <span>✓ {success}</span>
            </div>
          )}

          {error && (
            <div className="bg-red-50 text-red-700 p-4 rounded-xl mb-6 flex items-center gap-3 border border-red-200">
              <span>✕ {error}</span>
            </div>
          )}

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Profile Photo Section */}
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Profile Photo</h2>
              
              <div className="flex flex-col items-center">
                <div className="relative mb-6">
                  <div className="bg-gradient-to-b from-blue-600 to-blue-700 rounded-full shadow-lg w-32 h-32 flex items-center justify-center overflow-hidden">
                    {profileImage ? (
                      <img 
                        src={profileImage} 
                        alt="Profile" 
                        className="w-full h-full object-cover" 
                        onError={() => setProfileImage(null)} 
                      />
                    ) : (
                      <UserIcon className="text-white" size={48} strokeWidth={1.5} />
                    )}
                  </div>
                  
                  <label
                    htmlFor="profile-upload"
                    className="absolute bottom-0 right-0 bg-blue-600 rounded-full w-10 h-10 flex items-center justify-center shadow-lg cursor-pointer hover:bg-blue-700 transition-colors"
                  >
                    <Camera className="text-white" size={18} />
                    <input
                      id="profile-upload"
                      type="file"
                      accept="image/*"
                      onChange={handleImageChange}
                      className="hidden"
                      disabled={imageLoading}
                    />
                  </label>
                </div>

                <p className="font-semibold text-gray-900 text-base text-center mb-1">
                  {displayProfile.fullName}
                </p>
                <p className="text-sm text-gray-600 text-center mb-2">
                  {displayProfile.email}
                </p>
                <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold mb-4 ${
                  displayProfile.role === 'ADMIN' ? 'bg-red-100 text-red-700' :
                  displayProfile.role === 'STAFF' ? 'bg-purple-100 text-purple-700' :
                  'bg-blue-100 text-blue-700'
                }`}>
                  {displayProfile.role}
                </span>

                <button
                  onClick={() => document.getElementById("profile-upload")?.click()}
                  className="bg-blue-600 text-white rounded-xl px-6 py-2.5 text-sm font-semibold hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  disabled={imageLoading}
                >
                  {imageLoading ? 'Uploading...' : 'Change Photo'}
                </button>
                
                {profileImage && (
                  <button
                    onClick={() => setProfileImage(null)}
                    className="mt-2 text-red-600 text-sm font-medium hover:underline"
                  >
                    Remove Photo
                  </button>
                )}
              </div>
            </div>

            {/* Personal Information Section */}
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Personal Information</h2>
              
              <div className="space-y-5">
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 block">
                    Email Address
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500" size={18} />
                    <input
                      type="email"
                      value={formData.email}
                      disabled
                      className="w-full border border-gray-300 rounded-xl pl-12 pr-4 py-3 text-sm text-gray-500 bg-gray-50 cursor-not-allowed"
                    />
                  </div>
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 block">
                    Phone Number
                  </label>
                  <input
                    type="tel"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleInputChange}
                    placeholder="(555) 123-4567"
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                  />
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 block">
                    Address
                  </label>
                  <input
                    type="text"
                    name="address"
                    value={formData.address}
                    onChange={handleInputChange}
                    placeholder="123 Main Street"
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="font-semibold text-gray-900 text-sm mb-2 block">
                      City
                    </label>
                    <input
                      type="text"
                      name="city"
                      value={formData.city}
                      onChange={handleInputChange}
                      placeholder="New York"
                      className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                    />
                  </div>
                  <div>
                    <label className="font-semibold text-gray-900 text-sm mb-2 block">
                      Zip Code
                    </label>
                    <input
                      type="text"
                      name="zipCode"
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      placeholder="10001"
                      className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                    />
                  </div>
                </div>

                <button
                  onClick={handleSaveProfile}
                  className="w-full bg-blue-600 text-white rounded-xl py-3 text-sm font-semibold hover:bg-blue-700 transition-colors shadow-sm"
                >
                  Save Changes
                </button>
              </div>
            </div>

            {/* Change Password Section */}
            <div className="lg:col-span-2 bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-red-50 rounded-xl w-10 h-10 flex items-center justify-center">
                  <Lock className="text-red-600" size={20} />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-gray-900">Change Password</h2>
                  <p className="text-xs text-gray-600">Update your password to keep your account secure</p>
                </div>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 block">
                    Current Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500" size={18} />
                    <input
                      type={showCurrentPassword ? "text" : "password"}
                      name="currentPassword"
                      value={formData.currentPassword}
                      onChange={handleInputChange}
                      placeholder="Enter current password"
                      className="w-full border border-gray-300 rounded-xl pl-12 pr-12 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                    />
                    <button
                      type="button"
                      onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                      className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-900"
                    >
                      {showCurrentPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 block">
                    New Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500" size={18} />
                    <input
                      type={showNewPassword ? "text" : "password"}
                      name="newPassword"
                      value={formData.newPassword}
                      onChange={handleInputChange}
                      placeholder="Enter new password"
                      className="w-full border border-gray-300 rounded-xl pl-12 pr-12 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                    />
                    <button
                      type="button"
                      onClick={() => setShowNewPassword(!showNewPassword)}
                      className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-900"
                    >
                      {showNewPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 block">
                    Confirm New Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500" size={18} />
                    <input
                      type={showConfirmPassword ? "text" : "password"}
                      name="confirmPassword"
                      value={formData.confirmPassword}
                      onChange={handleInputChange}
                      placeholder="Confirm new password"
                      className="w-full border border-gray-300 rounded-xl pl-12 pr-12 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 transition-all"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-900"
                    >
                      {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                    </button>
                  </div>
                </div>
              </div>

              <div className="mt-5 bg-gray-50 border border-gray-200 rounded-xl p-4">
                <p className="font-semibold text-gray-900 text-xs mb-2">Password Requirements:</p>
                <ul className="space-y-1 text-xs text-gray-600">
                  <li className="flex items-center gap-2">
                    <span className={`w-1 h-1 rounded-full ${formData.newPassword.length >= 6 ? "bg-green-600" : "bg-gray-300"}`} />
                    At least 6 characters long
                  </li>
                  <li className="flex items-center gap-2">
                    <span className={`w-1 h-1 rounded-full ${/[A-Z]/.test(formData.newPassword) ? "bg-green-600" : "bg-gray-300"}`} />
                    Contains at least one uppercase letter
                  </li>
                  <li className="flex items-center gap-2">
                    <span className={`w-1 h-1 rounded-full ${/[0-9]/.test(formData.newPassword) ? "bg-green-600" : "bg-gray-300"}`} />
                    Contains at least one number
                  </li>
                </ul>
              </div>

              <button
                onClick={handleChangePassword}
                className="mt-5 w-full lg:w-auto bg-red-600 text-white rounded-xl px-8 py-3 text-sm font-semibold hover:bg-red-700 transition-colors shadow-sm"
              >
                Update Password
              </button>
            </div>
          </div>

          {/* Account Actions */}
          <div className="mt-6 bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Account Actions</h2>
            <div className="flex flex-wrap gap-3">
              <button 
                onClick={handleDownloadData}
                className="border border-gray-300 text-gray-700 rounded-xl px-6 py-2.5 text-sm font-medium hover:bg-gray-50 transition-colors"
              >
                Download My Data
              </button>
              <button 
                onClick={handlePrivacySettings}
                className="border border-gray-300 text-gray-700 rounded-xl px-6 py-2.5 text-sm font-medium hover:bg-gray-50 transition-colors"
              >
                Privacy Settings
              </button>
              <button 
                onClick={handleDeleteClick}
                className="border border-red-600 text-red-600 rounded-xl px-6 py-2.5 text-sm font-medium hover:bg-red-50 transition-colors"
              >
                Delete Account
              </button>
            </div>
          </div>
        </div>

        {/* Custom Privacy Settings Modal */}
        {showPrivacyModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-8 animate-in fade-in zoom-in duration-200">
              <div className="flex flex-col items-center text-center">
                <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mb-5">
                  <Shield className="text-blue-600" size={32} />
                </div>
                <h3 className="text-2xl font-bold text-gray-900 mb-3">Privacy Settings</h3>
                <p className="text-gray-600 mb-8 leading-relaxed">
                  Your privacy is important to us. Currently, all WashWise profiles are strictly private and your data is securely encrypted. 
                  <br /><br />
                  For further data management requests, please contact <a href="mailto:support@washwise.com" className="text-blue-600 font-medium hover:underline">support@washwise.com</a>.
                </p>
                
                <button
                  onClick={() => setShowPrivacyModal(false)}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3.5 rounded-xl transition-colors shadow-sm"
                >
                  Understood
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Custom Delete Confirmation Modal */}
        {showDeleteConfirm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 animate-in fade-in zoom-in duration-200">
              <div className="flex flex-col items-center text-center">
                <div className="bg-red-100 w-16 h-16 rounded-full flex items-center justify-center mb-4">
                  <AlertTriangle className="text-red-600" size={32} />
                </div>
                <h3 className="text-2xl font-bold text-gray-900 mb-2">Delete Account?</h3>
                <p className="text-gray-600 mb-6">
                  Are you absolutely sure you want to delete your account? This action <span className="font-bold text-gray-900">cannot be undone</span> and you will lose all your order history.
                </p>
                
                <div className="flex gap-3 w-full">
                  <button
                    onClick={() => setShowDeleteConfirm(false)}
                    className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-900 font-semibold py-3 rounded-xl transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={confirmDeleteAccount}
                    className="flex-1 bg-red-600 hover:bg-red-700 text-white font-semibold py-3 rounded-xl transition-colors shadow-sm"
                  >
                    Yes, Delete It
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}