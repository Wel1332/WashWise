import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Calendar, MapPin, FileText, DollarSign, Clock, AlertCircle, Loader, ArrowLeft, Shirt } from 'lucide-react';
import { servicesAPI, ordersAPI, reviewsAPI } from '../services/api';
import { useAuthStore } from '../store/authStore';
import ReviewForm from '../components/ReviewForm';
import ReviewList from '../components/ReviewList';

interface Service {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  duration: string;
}

interface Review {
  id: string;
  rating: number;
  comment: string;
  userName: string;
  createdAt: string;
}

interface ReviewInput {
  rating: number;
  comment: string;
  serviceId?: string;
}

export default function ServiceDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  
  // Existing Service State
  const [service, setService] = useState<Service | null>(null);
  const [loading, setLoading] = useState(true);
  const [orderLoading, setOrderLoading] = useState(false);
  const [location, setLocation] = useState('');
  const [notes, setNotes] = useState('');
  const [scheduledDate, setScheduledDate] = useState('');

  // New Reviews State
  const [reviews, setReviews] = useState<Review[]>([]);
  const [averageRating, setAverageRating] = useState(0);
  const [reviewCount, setReviewCount] = useState(0);

  const fetchService = useCallback(async () => {
    try {
      const { data } = await servicesAPI.getServiceById(id!);
      setService(data.data);
    } catch (err) {
      console.error('Failed to load service:', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    if (id) fetchService();
  }, [id, fetchService]);

  const fetchReviews = useCallback(async () => {
    if (!service?.id) return;
    
    try {
      const [reviewsRes, ratingRes] = await Promise.all([
        reviewsAPI.getServiceReviews(service.id),
        reviewsAPI.getAverageRating(service.id),
      ]);
      
      setReviews(reviewsRes.data.data);
      setAverageRating(ratingRes.data.data.averageRating);
      setReviewCount(ratingRes.data.data.reviewCount);
    } catch (err) {
      console.error('Failed to load reviews:', err);
    }
  }, [service?.id]);

  useEffect(() => {
    if (service?.id) {
      fetchReviews();
    }
  }, [service?.id, fetchReviews]);

  const handleBookService = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setOrderLoading(true);
    try {
      await ordersAPI.createOrder({
        serviceId: id,
        location,
        notes,
        scheduledDate: new Date(scheduledDate).toISOString(),
      });
      alert('Order placed successfully! We will pickup your clothes at the scheduled time.');
      navigate('/my-orders');
    } catch (err) {
      alert('Failed to place order');
      console.error(err);
    } finally {
      setOrderLoading(false);
    }
  };

  const handleSubmitReview = async (data: ReviewInput) => {
    try {
      await reviewsAPI.createReview(data as any); 
      fetchReviews();
    } catch (err) {
      console.error('Failed to submit review:', err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <Loader size={48} className="animate-spin mx-auto text-blue-600 mb-4" />
          <p className="text-xl text-gray-600">Loading service details...</p>
        </div>
      </div>
    );
  }

  if (!service) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
        <div className="bg-white p-8 rounded-xl border border-red-200 text-center max-w-md">
          <AlertCircle size={48} className="mx-auto text-red-500 mb-4" />
          <p className="text-xl text-red-600 font-semibold">Service not found</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <button
          onClick={() => navigate('/services')}
          className="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold mb-8"
        >
          <ArrowLeft size={20} />
          Back to Services
        </button>

        <div className="bg-white rounded-xl border border-gray-200 p-8 mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">{service.name}</h1>
          <p className="text-gray-600 text-lg mb-8">{service.description}</p>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8 pb-8 border-b border-gray-200">
            <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
              <p className="text-gray-600 text-sm font-semibold flex items-center gap-2 mb-2">
                <Shirt size={18} className="text-blue-600" /> Service Type
              </p>
              <p className="text-2xl font-bold text-gray-900">{service.category}</p>
            </div>
            <div className="bg-green-50 p-6 rounded-lg border border-green-200">
              <p className="text-gray-600 text-sm font-semibold flex items-center gap-2 mb-2">
                <DollarSign size={18} className="text-green-600" /> Price
              </p>
              <p className="text-2xl font-bold text-green-600">${service.price}</p>
            </div>
            <div className="bg-purple-50 p-6 rounded-lg border border-purple-200">
              <p className="text-gray-600 text-sm font-semibold flex items-center gap-2 mb-2">
                <Clock size={18} className="text-purple-600" /> Turnaround Time
              </p>
              <p className="text-2xl font-bold text-purple-600">{service.duration}</p>
            </div>
          </div>
        </div>

        {isAuthenticated && (
          <div className="bg-white rounded-xl border border-gray-200 p-8">
            <h2 className="text-2xl font-bold text-gray-900 mb-8 flex items-center gap-3">
              <div className="bg-blue-100 p-2 rounded-lg">
                <FileText size={28} className="text-blue-600" />
              </div>
              Book This Service
            </h2>
            
            <form onSubmit={handleBookService}>
              <div className="mb-6">
                <label className="block text-gray-900 mb-2 font-semibold flex items-center gap-2">
                  <MapPin size={18} className="text-red-600" />
                  Pickup Location
                </label>
                <input
                  type="text"
                  value={location}
                  onChange={(e) => setLocation(e.target.value)}
                  placeholder="Enter your address"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900"
                  required
                />
              </div>
              
              <div className="mb-6">
                <label className="block text-gray-900 mb-2 font-semibold flex items-center gap-2">
                  <Calendar size={18} className="text-blue-600" />
                  Scheduled Pickup Date & Time
                </label>
                <input
                  type="datetime-local"
                  value={scheduledDate}
                  onChange={(e) => setScheduledDate(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900"
                  required
                />
              </div>
              
              <div className="mb-8">
                <label className="block text-gray-900 mb-2 font-semibold flex items-center gap-2">
                  <FileText size={18} className="text-gray-600" />
                  Special Instructions (Optional)
                </label>
                <textarea
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  placeholder="Any special garments or care instructions? Let us know..."
                  rows={4}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900"
                />
              </div>
              
              <button
                type="submit"
                disabled={orderLoading}
                className="w-full bg-green-600 hover:bg-green-700 text-white py-4 rounded-lg font-semibold disabled:opacity-50 flex items-center justify-center gap-2 transition text-lg"
              >
                {orderLoading ? (
                  <>
                    <Loader size={20} className="animate-spin" />
                    Processing...
                  </>
                ) : (
                  <>
                    <DollarSign size={20} />
                    Book Service - ${service.price}
                  </>
                )}
              </button>
            </form>
          </div>
        )}

        {!isAuthenticated && (
          <div className="bg-yellow-50 border-2 border-yellow-200 p-8 rounded-xl text-center">
            <AlertCircle size={48} className="mx-auto text-yellow-600 mb-4" />
            <p className="text-gray-900 mb-4 text-lg font-semibold">Sign in to book this service</p>
            <p className="text-gray-600 mb-6">Create an account or log in to book your laundry service</p>
            <button
              onClick={() => navigate('/login')}
              className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg font-semibold transition inline-flex items-center gap-2"
            >
              <FileText size={18} />
              Sign In Now
            </button>
          </div>
        )}

        {/* NEW REVIEWS SECTION */}
        <div className="mt-12">
          <h2 className="text-3xl font-bold text-gray-900 mb-8">Customer Reviews</h2>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2">
              <ReviewList 
                reviews={reviews} 
                averageRating={averageRating}
                reviewCount={reviewCount}
              />
            </div>
            <div>
              {isAuthenticated && (
                <ReviewForm 
                  serviceId={service.id} 
                  onSubmit={handleSubmitReview}
                />
              )}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}