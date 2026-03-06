import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Mail, Phone, MapPin, FileText, Camera, Loader, AlertCircle, CheckCircle } from 'lucide-react';
import { profileAPI } from '../services/api';
import { useAuthStore } from '../store/authStore';

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
  profileImageUrl?: string;
}

export default function UserProfile() {
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [imageLoading, setImageLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState({
    bio: '',
    phoneNumber: '',
    address: '',
    city: '',
    zipCode: '',
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
        bio: profileData.bio || '',
        phoneNumber: profileData.phoneNumber || '',
        address: profileData.address || '',
        city: profileData.city || '',
        zipCode: profileData.zipCode || '',
      });
    } catch (err: any) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const { data } = await profileAPI.updateProfile(formData);
      setProfile(data.data);
      setSuccess('Profile updated successfully!');
      setEditing(false);
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile');
    }
  };

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setImageLoading(true);
    try {
      const { data } = await profileAPI.uploadProfileImage(file);
      setProfile(data.data);
      setSuccess('Profile image updated!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError('Failed to upload image');
    } finally {
      setImageLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Loader size={48} className="animate-spin text-blue-600" />
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white p-8 rounded-xl border border-red-200 text-center">
          <AlertCircle size={48} className="mx-auto text-red-600 mb-4" />
          <p className="text-xl text-red-600 font-semibold">Failed to load profile</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white">
      <div className="max-w-4xl mx-auto px-4 py-16">
        {/* HEADER */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">My Profile</h1>
          <p className="text-gray-600">Manage your account information and settings</p>
        </div>

        {/* ALERTS */}
        {success && (
          <div className="bg-green-50 text-green-700 p-4 rounded-lg mb-6 flex items-center gap-3 border border-green-200">
            <CheckCircle size={20} />
            {success}
          </div>
        )}

        {error && (
          <div className="bg-red-50 text-red-700 p-4 rounded-lg mb-6 flex items-center gap-3 border border-red-200">
            <AlertCircle size={20} />
            {error}
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* PROFILE IMAGE SECTION */}
          <div className="lg:col-span-1">
            <div className="bg-gradient-to-br from-blue-50 to-purple-50 rounded-xl border-2 border-blue-200 p-8 text-center">
              {/* Profile Image */}
              <div className="relative w-40 h-40 mx-auto mb-6">
                {profile.profileImageUrl ? (
                  <img
                    src={`http://localhost:8080${profile.profileImageUrl}`}
                    alt={profile.fullName}
                    className="w-full h-full rounded-full object-cover border-4 border-white shadow-lg"
                  />
                ) : (
                  <div className="w-full h-full rounded-full bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center border-4 border-white shadow-lg">
                    <User size={80} className="text-white" />
                  </div>
                )}

                {/* Upload Image Button */}
                <label className="absolute bottom-0 right-0 bg-blue-600 hover:bg-blue-700 text-white p-3 rounded-full cursor-pointer transition shadow-lg">
                  {imageLoading ? (
                    <Loader size={20} className="animate-spin" />
                  ) : (
                    <Camera size={20} />
                  )}
                  <input
                    type="file"
                    onChange={handleImageUpload}
                    accept="image/*"
                    className="hidden"
                    disabled={imageLoading}
                  />
                </label>
              </div>

              {/* User Info Card */}
              <div className="bg-white rounded-lg p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-1">{profile.fullName}</h2>
                <p className="text-gray-600 mb-4">{profile.email}</p>
                <span className={`inline-block px-4 py-2 rounded-full text-sm font-bold ${
                  profile.role === 'ADMIN' ? 'bg-red-100 text-red-700' :
                  profile.role === 'STAFF' ? 'bg-purple-100 text-purple-700' :
                  'bg-blue-100 text-blue-700'
                }`}>
                  {profile.role}
                </span>
              </div>
            </div>
          </div>

          {/* PROFILE DETAILS SECTION */}
          <div className="lg:col-span-2">
            {!editing ? (
              <div className="bg-gray-50 rounded-xl border border-gray-200 p-8">
                <h3 className="text-2xl font-bold text-gray-900 mb-8 flex items-center gap-3">
                  <FileText size={28} className="text-blue-600" />
                  Profile Information
                </h3>

                <div className="space-y-6">
                  {/* Email */}
                  <div>
                    <label className="block text-gray-600 text-sm font-semibold mb-2 flex items-center gap-2">
                      <Mail size={18} className="text-blue-600" /> Email
                    </label>
                    <p className="text-gray-900 font-medium">{profile.email}</p>
                  </div>

                  {/* Phone */}
                  <div>
                    <label className="block text-gray-600 text-sm font-semibold mb-2 flex items-center gap-2">
                      <Phone size={18} className="text-blue-600" /> Phone Number
                    </label>
                    <p className="text-gray-900 font-medium">{profile.phoneNumber || 'Not provided'}</p>
                  </div>

                  {/* Address */}
                  <div>
                    <label className="block text-gray-600 text-sm font-semibold mb-2 flex items-center gap-2">
                      <MapPin size={18} className="text-blue-600" /> Address
                    </label>
                    <p className="text-gray-900 font-medium">{profile.address || 'Not provided'}</p>
                  </div>

                  {/* City */}
                  <div>
                    <label className="block text-gray-600 text-sm font-semibold mb-2">City</label>
                    <p className="text-gray-900 font-medium">{profile.city || 'Not provided'}</p>
                  </div>

                  {/* Zip Code */}
                  <div>
                    <label className="block text-gray-600 text-sm font-semibold mb-2">Zip Code</label>
                    <p className="text-gray-900 font-medium">{profile.zipCode || 'Not provided'}</p>
                  </div>

                  {/* Bio */}
                  <div>
                    <label className="block text-gray-600 text-sm font-semibold mb-2">Bio</label>
                    <p className="text-gray-900 font-medium">{profile.bio || 'No bio provided'}</p>
                  </div>
                </div>

                <button
                  onClick={() => setEditing(true)}
                  className="w-full mt-8 bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition"
                >
                  Edit Profile
                </button>
              </div>
            ) : (
              /* EDIT FORM */
              <div className="bg-gray-50 rounded-xl border border-gray-200 p-8">
                <h3 className="text-2xl font-bold text-gray-900 mb-8">Edit Profile</h3>

                <form onSubmit={handleSubmit} className="space-y-6">
                  {/* Phone Number */}
                  <div>
                    <label className="block text-gray-900 mb-2 font-semibold flex items-center gap-2">
                      <Phone size={18} className="text-blue-600" /> Phone Number
                    </label>
                    <input
                      type="tel"
                      name="phoneNumber"
                      value={formData.phoneNumber}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                      placeholder="(555) 123-4567"
                    />
                  </div>

                  {/* Address */}
                  <div>
                    <label className="block text-gray-900 mb-2 font-semibold flex items-center gap-2">
                      <MapPin size={18} className="text-blue-600" /> Address
                    </label>
                    <input
                      type="text"
                      name="address"
                      value={formData.address}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                      placeholder="123 Main Street"
                    />
                  </div>

                  {/* City */}
                  <div>
                    <label className="block text-gray-900 mb-2 font-semibold">City</label>
                    <input
                      type="text"
                      name="city"
                      value={formData.city}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                      placeholder="New York"
                    />
                  </div>

                  {/* Zip Code */}
                  <div>
                    <label className="block text-gray-900 mb-2 font-semibold">Zip Code</label>
                    <input
                      type="text"
                      name="zipCode"
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                      placeholder="10001"
                    />
                  </div>

                  {/* Bio */}
                  <div>
                    <label className="block text-gray-900 mb-2 font-semibold">Bio</label>
                    <textarea
                      name="bio"
                      value={formData.bio}
                      onChange={handleInputChange}
                      rows={4}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                      placeholder="Tell us about yourself..."
                    />
                    <p className="text-gray-500 text-sm mt-1">{formData.bio.length}/200</p>
                  </div>

                  {/* Buttons */}
                  <div className="flex gap-4 pt-4">
                    <button
                      type="submit"
                      className="flex-1 bg-green-600 hover:bg-green-700 text-white py-3 rounded-lg font-semibold transition"
                    >
                      Save Changes
                    </button>
                    <button
                      type="button"
                      onClick={() => setEditing(false)}
                      className="flex-1 bg-gray-400 hover:bg-gray-500 text-white py-3 rounded-lg font-semibold transition"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}