import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { 
  Calendar, 
  Clock, 
  MapPin, 
  Shirt, 
  Sparkles, 
  Wind, 
  Package as PackageIcon,
  Scale,
  AlertCircle
} from "lucide-react";
import { useAuthStore } from '../store/authStore';
import { ordersAPI } from '../services/api';
import Sidebar from '../components/Sidebar';

type ServiceType = "wash-fold" | "dry-clean" | "iron-press" | "premium";

interface Service {
  id: ServiceType;
  name: string;
  description: string;
  icon: React.ReactNode;
  pricePerKg: number;
  duration: string;
  color: string;
  bgColor: string;
}

const services: Service[] = [
  {
    id: "wash-fold",
    name: "Wash & Fold",
    description: "Perfect for everyday laundry",
    icon: <Shirt size={28} />,
    pricePerKg: 3.30,
    duration: "24-48 hours",
    color: "#007bff",
    bgColor: "#eff6ff",
  },
  {
    id: "dry-clean",
    name: "Dry Cleaning",
    description: "Professional care for delicates",
    icon: <Sparkles size={28} />,
    pricePerKg: 19.99,
    duration: "2-3 days",
    color: "#9810FA",
    bgColor: "#faf5ff",
  },
  {
    id: "iron-press",
    name: "Iron & Press",
    description: "Crisp and wrinkle-free",
    icon: <Wind size={28} />,
    pricePerKg: 7.70,
    duration: "24 hours",
    color: "#00a63e",
    bgColor: "#f0fdf4",
  },
  {
    id: "premium",
    name: "Premium Care",
    description: "Special handling for luxury items",
    icon: <PackageIcon size={28} />,
    pricePerKg: 35.00,
    duration: "3-5 days",
    color: "#FF6B35",
    bgColor: "#fff5f3",
  },
];

export default function BookService() {
  const navigate = useNavigate();
  const [selectedService, setSelectedService] = useState<ServiceType | null>(null);
  const [weight, setWeight] = useState<string>("");
  const [pickupDate, setPickupDate] = useState("");
  const [pickupTime, setPickupTime] = useState("");
  const [deliveryDate, setDeliveryDate] = useState("");
  const [deliveryTime, setDeliveryTime] = useState("");
  const [address, setAddress] = useState("123 Main Street, Apt 4B");
  const [specialInstructions, setSpecialInstructions] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const calculatePrice = () => {
    if (!selectedService || !weight || parseFloat(weight) <= 0) return 0;
    const service = services.find(s => s.id === selectedService);
    if (!service) return 0;
    return service.pricePerKg * parseFloat(weight);
  };

  const totalPrice = calculatePrice();

  const handleBookService = async () => {
    // Validation
    if (!selectedService || !pickupDate || !pickupTime || !weight || parseFloat(weight) <= 0) {
      setError("Please select a service, weight, and pickup time");
      return;
    }

    if (!address.trim()) {
      setError("Please provide a pickup address");
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
  
      // Map service type to backend service UUID
      // REPLACE WITH YOUR ACTUAL UUIDs FROM THE DATABASE!
      const serviceIdMap: Record<ServiceType, string> = {
        "wash-fold": "jkl-012-mno...",      // Copy UUID from Wash & Fold
        "dry-clean": "abc-123-def...",      // Copy UUID from Dry Cleaning
        "iron-press": "def-456-ghi...",     // Copy UUID from Iron & Press
        "premium": "ghi-789-jkl..."         // Copy UUID from Premium Care
      };
  
      const orderData = {
        serviceId: serviceIdMap[selectedService],
        weightKg: parseFloat(weight),
        totalPrice: totalPrice,
        pickupAddress: address,
        pickupDate: pickupDate,
        pickupTimeSlot: pickupTime,
        deliveryDate: deliveryDate || pickupDate,
        deliveryTimeSlot: deliveryTime || pickupTime,
        specialInstructions: specialInstructions.trim() || null,
        status: "PENDING"
      };
  
      const response = await ordersAPI.createOrder(orderData);
  
      alert(`Service booked successfully! Total: $${totalPrice.toFixed(2)}\nOrder ID: ${response.data.data?.id || 'N/A'}\nWe'll send you a confirmation email.`);
      
      // Clear form
      setSelectedService(null);
      setWeight("");
      setPickupDate("");
      setPickupTime("");
      setDeliveryDate("");
      setDeliveryTime("");
      setSpecialInstructions("");
      
      // Redirect to orders page
      setTimeout(() => {
        navigate('/my-orders');
      }, 1500);
  
    } catch (error: any) {
      console.error('Failed to create order:', error);
      setError(error.response?.data?.message || 'Failed to book service. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <Sidebar userRole="CUSTOMER" activePage="book-service" />

      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        <div className="p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Book a Service</h1>
            <p className="text-lg text-gray-600">Schedule your laundry pickup and delivery</p>
          </div>

          {/* Error Message */}
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 rounded-2xl p-4 flex items-start gap-3">
              <AlertCircle className="text-red-600 flex-shrink-0" size={20} />
              <div>
                <p className="text-red-900 font-medium">{error}</p>
              </div>
              <button
                onClick={() => setError(null)}
                className="ml-auto text-red-600 hover:text-red-700"
              >
                ✕
              </button>
            </div>
          )}

          {/* Service Selection */}
          <div className="mb-8">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Select Service Type</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              {services.map((service) => (
                <button
                  key={service.id}
                  onClick={() => setSelectedService(service.id)}
                  className={`bg-white border-2 rounded-2xl p-6 text-left transition-all hover:shadow-lg ${
                    selectedService === service.id
                      ? 'shadow-lg'
                      : 'border-gray-200 shadow-sm'
                  }`}
                  style={{
                    borderColor: selectedService === service.id ? service.color : '#dee2e6',
                  }}
                >
                  <div
                    className="rounded-xl w-14 h-14 flex items-center justify-center mb-4"
                    style={{
                      backgroundColor: service.bgColor,
                      color: service.color,
                    }}
                  >
                    {service.icon}
                  </div>
                  <h3 className="font-semibold text-gray-900 text-base mb-1">
                    {service.name}
                  </h3>
                  <p className="text-gray-600 text-xs mb-3">
                    {service.description}
                  </p>
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-gray-900 text-sm">
                      ${service.pricePerKg.toFixed(2)}/kg
                    </span>
                    <span className="text-gray-600 text-xs">
                      {service.duration}
                    </span>
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Weight Input */}
          <div className="mb-8">
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-purple-100 rounded-xl w-10 h-10 flex items-center justify-center">
                  <Scale className="text-purple-600" size={20} />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-gray-900">Laundry Weight</h2>
                  <p className="text-gray-600 text-xs">Enter the estimated weight of your laundry</p>
                </div>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <Scale size={16} className="text-gray-600" />
                    Weight (kg)
                  </label>
                  <input
                    type="number"
                    value={weight}
                    onChange={(e) => setWeight(e.target.value)}
                    min="0"
                    step="0.1"
                    placeholder="e.g., 5.0"
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
                  />
                  <p className="text-gray-600 text-xs mt-2">
                    Tip: 1 kg ≈ 2-3 shirts or 1 pair of jeans
                  </p>
                </div>

                {/* Price Calculation Display */}
                {selectedService && weight && parseFloat(weight) > 0 && (
                  <div className="bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-200 rounded-xl p-6 flex flex-col justify-center">
                    <p className="font-semibold text-gray-600 text-xs uppercase tracking-wide mb-2">
                      Estimated Total
                    </p>
                    <div className="flex items-baseline gap-2">
                      <span className="font-bold text-gray-900 text-4xl">
                        ${totalPrice.toFixed(2)}
                      </span>
                      <span className="text-gray-600 text-sm">
                        ({weight} kg × ${services.find(s => s.id === selectedService)?.pricePerKg.toFixed(2)}/kg)
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Pickup Details */}
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-blue-100 rounded-xl w-10 h-10 flex items-center justify-center">
                  <Calendar className="text-blue-600" size={20} />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-gray-900">Pickup Details</h2>
                  <p className="text-gray-600 text-xs">When should we collect your laundry?</p>
                </div>
              </div>

              <div className="space-y-5">
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <Calendar size={16} className="text-gray-600" />
                    Pickup Date
                  </label>
                  <input
                    type="date"
                    value={pickupDate}
                    onChange={(e) => setPickupDate(e.target.value)}
                    min={new Date().toISOString().split('T')[0]}
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
                  />
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <Clock size={16} className="text-gray-600" />
                    Pickup Time
                  </label>
                  <select
                    value={pickupTime}
                    onChange={(e) => setPickupTime(e.target.value)}
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
                  >
                    <option value="">Select time slot</option>
                    <option value="8-10">8:00 AM - 10:00 AM</option>
                    <option value="10-12">10:00 AM - 12:00 PM</option>
                    <option value="12-14">12:00 PM - 2:00 PM</option>
                    <option value="14-16">2:00 PM - 4:00 PM</option>
                    <option value="16-18">4:00 PM - 6:00 PM</option>
                    <option value="18-20">6:00 PM - 8:00 PM</option>
                  </select>
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <MapPin size={16} className="text-gray-600" />
                    Pickup Address
                  </label>
                  <input
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
                  />
                </div>
              </div>
            </div>

            {/* Delivery Details */}
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-green-100 rounded-xl w-10 h-10 flex items-center justify-center">
                  <PackageIcon className="text-green-600" size={20} />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-gray-900">Delivery Details</h2>
                  <p className="text-gray-600 text-xs">When should we return your laundry?</p>
                </div>
              </div>

              <div className="space-y-5">
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <Calendar size={16} className="text-gray-600" />
                    Delivery Date
                  </label>
                  <input
                    type="date"
                    value={deliveryDate}
                    onChange={(e) => setDeliveryDate(e.target.value)}
                    min={pickupDate || new Date().toISOString().split('T')[0]}
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
                  />
                </div>

                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <Clock size={16} className="text-gray-600" />
                    Delivery Time
                  </label>
                  <select
                    value={deliveryTime}
                    onChange={(e) => setDeliveryTime(e.target.value)}
                    className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20"
                  >
                    <option value="">Select time slot</option>
                    <option value="8-10">8:00 AM - 10:00 AM</option>
                    <option value="10-12">10:00 AM - 12:00 PM</option>
                    <option value="12-14">12:00 PM - 2:00 PM</option>
                    <option value="14-16">2:00 PM - 4:00 PM</option>
                    <option value="16-18">4:00 PM - 6:00 PM</option>
                    <option value="18-20">6:00 PM - 8:00 PM</option>
                  </select>
                </div>

                <button
                  onClick={() => {
                    setDeliveryDate(pickupDate);
                    setDeliveryTime(pickupTime);
                  }}
                  className="text-blue-600 font-medium text-sm hover:underline"
                >
                  Use same date/time as pickup
                </button>
              </div>
            </div>

            {/* Special Instructions */}
            <div className="lg:col-span-2 bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Special Instructions</h2>
              <textarea
                value={specialInstructions}
                onChange={(e) => setSpecialInstructions(e.target.value)}
                placeholder="Add any special instructions for handling your laundry (e.g., fragrance preferences, stain removal notes, delicate items)"
                rows={4}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 focus:outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-600/20 resize-none"
              />
            </div>
          </div>

          {/* Order Summary */}
          <div className="mt-6 bg-gradient-to-br from-blue-600 to-blue-700 rounded-2xl shadow-lg p-8">
            <div className="flex flex-col lg:flex-row items-center justify-between gap-6">
              <div className="text-white flex-1">
                <h2 className="text-2xl font-bold mb-1">
                  {selectedService ? services.find(s => s.id === selectedService)?.name : "Select a Service"}
                </h2>
                <p className="text-blue-100 text-sm mb-3">
                  {selectedService && pickupDate && pickupTime
                    ? `Pickup: ${pickupDate} (${pickupTime})`
                    : "Complete the form to book your service"}
                </p>
                {weight && parseFloat(weight) > 0 && selectedService && (
                  <div className="flex items-baseline gap-3">
                    <span className="font-bold text-white text-3xl">
                      ${totalPrice.toFixed(2)}
                    </span>
                    <span className="text-blue-100 text-sm">
                      for {weight} kg
                    </span>
                  </div>
                )}
              </div>
              <div className="flex gap-3">
                <button
                  onClick={() => {
                    setSelectedService(null);
                    setWeight("");
                    setPickupDate("");
                    setPickupTime("");
                    setDeliveryDate("");
                    setDeliveryTime("");
                    setSpecialInstructions("");
                    setError(null);
                  }}
                  disabled={isSubmitting}
                  className="bg-white/10 text-white border border-white/30 rounded-xl px-6 py-3 text-sm font-medium hover:bg-white/20 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Clear
                </button>
                <button
                  onClick={handleBookService}
                  disabled={!selectedService || !pickupDate || !pickupTime || !weight || parseFloat(weight) <= 0 || isSubmitting}
                  className="bg-white text-blue-600 rounded-xl px-8 py-3 text-sm font-semibold hover:bg-blue-50 transition-colors shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                >
                  {isSubmitting ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                      Processing...
                    </>
                  ) : (
                    'Book Service'
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}