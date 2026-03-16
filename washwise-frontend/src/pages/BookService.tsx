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
  LayoutDashboard,
  ShoppingCart,
  UserCircle,
  LogOut,
  Droplets,
  User as UserIcon,
  Scale
} from "lucide-react";
import { useAuthStore } from '../store/authStore';

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
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [selectedService, setSelectedService] = useState<ServiceType | null>(null);
  const [weight, setWeight] = useState<string>("");
  const [pickupDate, setPickupDate] = useState("");
  const [pickupTime, setPickupTime] = useState("");
  const [deliveryDate, setDeliveryDate] = useState("");
  const [deliveryTime, setDeliveryTime] = useState("");
  const [address, setAddress] = useState("123 Main Street, Apt 4B");
  const [specialInstructions, setSpecialInstructions] = useState("");

  const calculatePrice = () => {
    if (!selectedService || !weight || parseFloat(weight) <= 0) return 0;
    const service = services.find(s => s.id === selectedService);
    if (!service) return 0;
    return service.pricePerKg * parseFloat(weight);
  };

  const totalPrice = calculatePrice();

  const handleBookService = () => {
    if (!selectedService || !pickupDate || !pickupTime || !weight || parseFloat(weight) <= 0) {
      alert("Please select a service, weight, and pickup time");
      return;
    }
    alert(`Service booked successfully! Total: $${totalPrice.toFixed(2)}\nWe'll send you a confirmation email.`);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col flex-shrink-0">
        {/* Logo */}
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

        {/* Navigation */}
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
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium bg-blue-600 text-white transition-all"
            >
              <ShoppingCart size={20} />
              <span>Book Service</span>
            </button>

            <button
              onClick={() => navigate('/my-orders')}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-gray-700 hover:bg-gray-100 transition-all"
            >
              <PackageIcon size={20} />
              <span>My Orders</span>
            </button>

            <button
              onClick={() => navigate('/profile')}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-gray-700 hover:bg-gray-100 transition-all"
            >
              <UserCircle size={20} />
              <span>Profile</span>
            </button>
          </div>
        </nav>

        {/* User Profile */}
        <div className="p-4 border-t border-gray-200">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
              <UserIcon className="text-white" size={24} />
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-semibold text-gray-900 text-sm truncate">{user?.fullName || 'Customer'}</p>
              <p className="text-xs text-gray-500 truncate">{user?.email}</p>
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
      <main className="flex-1 overflow-auto">
        <div className="p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Book a Service</h1>
            <p className="text-lg text-gray-600">Schedule your laundry pickup and delivery</p>
          </div>

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
                  Use same address as pickup
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
                  }}
                  className="bg-white/10 text-white border border-white/30 rounded-xl px-6 py-3 text-sm font-medium hover:bg-white/20 transition-colors"
                >
                  Clear
                </button>
                <button
                  onClick={handleBookService}
                  disabled={!selectedService || !pickupDate || !pickupTime || !weight || parseFloat(weight) <= 0}
                  className="bg-white text-blue-600 rounded-xl px-8 py-3 text-sm font-semibold hover:bg-blue-50 transition-colors shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Book Service
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}