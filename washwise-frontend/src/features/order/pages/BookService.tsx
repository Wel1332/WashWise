import { useState, useMemo, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { 
  Calendar, 
  Clock, 
  MapPin, 
  Shirt, 
  Sparkles, 
  Droplets,
  Package as PackageIcon,
  Scale,
  AlertCircle,
  CheckCircle2
} from "lucide-react";
import { ordersAPI } from '../../../shared/services/api';
import Sidebar from '../../../shared/components/Sidebar';

type ServiceType = "wash-only" | "wash-dry-fold" | "dry-clean" | "premium";

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
    id: "wash-only",
    name: "Wash Only",
    description: "Basic washing for everyday items",
    icon: <Droplets size={28} />,
    pricePerKg: 30, 
    duration: "24-48 hours",
    color: "#0891b2", 
    bgColor: "#cffafe",
  },
  {
    id: "wash-dry-fold",
    name: "Wash-Dry-Fold",
    description: "Complete everyday laundry care",
    icon: <Shirt size={28} />,
    pricePerKg: 40,
    duration: "2-3 days",
    color: "#007bff",
    bgColor: "#eff6ff",
  },
  {
    id: "dry-clean",
    name: "Dry Cleaning",
    description: "Professional care for delicates",
    icon: <Sparkles size={28} />,
    pricePerKg: 150, 
    duration: "3-5 days",
    color: "#9810FA",
    bgColor: "#faf5ff",
  },
  {
    id: "premium",
    name: "Premium Care",
    description: "Special handling for luxury items",
    icon: <PackageIcon size={28} />,
    pricePerKg: 175, 
    duration: "5-7 days",
    color: "#FF6B35",
    bgColor: "#fff5f3",
  },
];

const TIME_SLOTS = [
  { id: "8-10", label: "08:00 AM - 10:00 AM" },
  { id: "10-12", label: "10:00 AM - 12:00 PM" },
  { id: "12-14", label: "12:00 PM - 02:00 PM" },
  { id: "14-16", label: "02:00 PM - 04:00 PM" },
  { id: "16-18", label: "04:00 PM - 06:00 PM" },
  { id: "18-20", label: "06:00 PM - 08:00 PM" },
];

// Helper to determine minimum delivery days based on service
const getMinDeliveryDays = (serviceId: ServiceType | null) => {
  switch (serviceId) {
    case "wash-only": return 1; // Minimum 24 hours
    case "wash-dry-fold": return 2; // Minimum 2 days
    case "dry-clean": return 3; // Minimum 3 days
    case "premium": return 5; // Minimum 5 days
    default: return 0;
  }
};

// Updated to accept an offset for minimum delivery dates
const generateDateOptions = (startDateStr: string | null, daysCount: number = 7, minOffsetDays: number = 0) => {
  const dates = [];
  const start = startDateStr ? new Date(startDateStr) : new Date();
  
  for (let i = 0; i < daysCount; i++) {
    const d = new Date(start);
    // Add the offset (e.g., jump 5 days ahead for premium)
    d.setDate(start.getDate() + i + minOffsetDays);
    
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const value = `${year}-${month}-${day}`;
    
    const dayName = d.toLocaleDateString('en-US', { weekday: 'short' }); 
    const dayNum = d.toLocaleDateString('en-US', { day: 'numeric' });    
    const monthName = d.toLocaleDateString('en-US', { month: 'short' }); 

    dates.push({ value, dayName, dayNum, monthName, isToday: i === 0 && !startDateStr && minOffsetDays === 0 });
  }
  return dates;
};

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
  const [bookingSuccess, setBookingSuccess] = useState<{ show: boolean; total: number; orderId: string } | null>(null);

  const [bookedPickupSlots] = useState<string[]>([]); 

  // Calculate minimum days required for the selected service
  const minDeliveryDays = getMinDeliveryDays(selectedService);

  // Generate Date Sliders dynamically
  const pickupDateOptions = useMemo(() => generateDateOptions(null, 7), []);
  
  // Delivery dates now start at (Pickup Date + minDeliveryDays)
  const deliveryDateOptions = useMemo(() => 
    generateDateOptions(pickupDate || null, 7, pickupDate ? minDeliveryDays : 0), 
  [pickupDate, minDeliveryDays]);

  // If user changes service type, reset their delivery date if it's now invalid
  useEffect(() => {
    setDeliveryDate("");
    setDeliveryTime("");
  }, [selectedService]);

  const calculatePrice = () => {
    if (!selectedService || !weight || parseFloat(weight) <= 0) return 0;
    const service = services.find(s => s.id === selectedService);
    if (!service) return 0;
    return service.pricePerKg * parseFloat(weight);
  };

  const totalPrice = calculatePrice();

  const handleBookService = async () => {
    if (!selectedService || !pickupDate || !pickupTime || !deliveryDate || !deliveryTime || !weight || parseFloat(weight) <= 0) {
      setError("Please fill out all required fields, including pickup and delivery times.");
      return;
    }

    if (!address.trim()) {
      setError("Please provide a pickup address");
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
  
      const serviceIdMap: Record<ServiceType, string> = {
        "wash-only": "74697372-4ab5-461e-bbd7-7e8826ac5b06", 
        "wash-dry-fold": "583ea981-df5b-45a3-ab9d-47e9a2f0965a",      
        "dry-clean": "26f2fa14-99b7-4593-9df8-f2b004f2b9cd",      
        "premium": "78197d33-1beb-406c-acef-4c92132e6d49"         
      };
  
      const orderData = {
        serviceId: serviceIdMap[selectedService],
        weightKg: parseFloat(weight),
        totalPrice: totalPrice,
        pickupAddress: address,
        pickupDate: pickupDate,
        pickupTimeSlot: pickupTime,
        deliveryDate: deliveryDate,
        deliveryTimeSlot: deliveryTime,
        specialInstructions: specialInstructions.trim() || null,
        status: "PENDING"
      };
  
      const response = await ordersAPI.createOrder(orderData);
  
      setBookingSuccess({
        show: true,
        total: totalPrice,
        orderId: response.data.data?.id || 'N/A'
      });
      
      setSelectedService(null);
      setWeight("");
      setPickupDate("");
      setPickupTime("");
      setDeliveryDate("");
      setDeliveryTime("");
      setSpecialInstructions("");
  
    } catch (error: any) {
      console.error('Failed to create order:', error);
      setError(error.response?.data?.message || 'Failed to book service. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Auto-select fastest delivery option
  const handleFastestDelivery = () => {
    if (pickupDate && deliveryDateOptions.length > 0) {
      setDeliveryDate(deliveryDateOptions[0].value);
      setDeliveryTime("8-10"); // Default to morning
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <Sidebar userRole="CUSTOMER" activePage="book-service" />

      <main className="flex-1 overflow-auto relative">
        <div className="p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Book a Service</h1>
            <p className="text-lg text-gray-600">Schedule your laundry pickup and delivery</p>
          </div>

          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 rounded-2xl p-4 flex items-start gap-3">
              <AlertCircle className="text-red-600 flex-shrink-0" size={20} />
              <div>
                <p className="text-red-900 font-medium">{error}</p>
              </div>
              <button onClick={() => setError(null)} className="ml-auto text-red-600 hover:text-red-700">✕</button>
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
                      ? 'shadow-lg scale-[1.02]'
                      : 'border-gray-200 shadow-sm'
                  }`}
                  style={{ borderColor: selectedService === service.id ? service.color : '#dee2e6' }}
                >
                  <div
                    className="rounded-xl w-14 h-14 flex items-center justify-center mb-4 transition-transform"
                    style={{
                      backgroundColor: service.bgColor,
                      color: service.color,
                      transform: selectedService === service.id ? 'scale(1.1)' : 'scale(1)',
                    }}
                  >
                    {service.icon}
                  </div>
                  <h3 className="font-semibold text-gray-900 text-base mb-1">{service.name}</h3>
                  <p className="text-gray-600 text-xs mb-3">{service.description}</p>
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-gray-900 text-sm">₱{service.pricePerKg.toFixed(0)}/kg</span>
                    <span className="text-gray-600 text-xs">{service.duration}</span>
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
                  <p className="text-gray-600 text-xs mt-2">Tip: 1 kg ≈ 2-3 shirts or 1 pair of jeans</p>
                </div>

                {selectedService && weight && parseFloat(weight) > 0 && (
                  <div className="bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-200 rounded-xl p-6 flex flex-col justify-center">
                    <p className="font-semibold text-gray-600 text-xs uppercase tracking-wide mb-2">Estimated Total</p>
                    <div className="flex items-baseline gap-2">
                      <span className="font-bold text-gray-900 text-4xl">₱{totalPrice.toFixed(0)}</span>
                      <span className="text-gray-600 text-sm">
                        ({weight} kg × ₱{services.find(s => s.id === selectedService)?.pricePerKg.toFixed(0)}/kg)
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
            
            {/* ====== PICKUP DETAILS ====== */}
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

              <div className="space-y-6">
                {/* Modern Date Slider */}
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-3 flex items-center gap-2">
                    <Calendar size={16} className="text-gray-600" />
                    Pickup Date
                  </label>
                  <div className="flex gap-3 overflow-x-auto pb-2 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
                    {pickupDateOptions.map((dateObj) => (
                      <button
                        key={`pickup-${dateObj.value}`}
                        onClick={() => setPickupDate(dateObj.value)}
                        className={`flex-shrink-0 flex flex-col items-center justify-center w-20 h-24 rounded-2xl border-2 transition-all ${
                          pickupDate === dateObj.value
                            ? 'bg-blue-600 border-blue-600 text-white shadow-md scale-[1.02]'
                            : 'bg-white border-gray-200 text-gray-700 hover:border-blue-300 hover:bg-blue-50'
                        }`}
                      >
                        <span className={`text-xs font-medium uppercase ${pickupDate === dateObj.value ? 'text-blue-100' : 'text-gray-500'}`}>
                          {dateObj.dayName}
                        </span>
                        <span className="text-2xl font-bold my-0.5">{dateObj.dayNum}</span>
                        <span className={`text-xs ${pickupDate === dateObj.value ? 'text-blue-100' : 'text-gray-500'}`}>
                          {dateObj.monthName}
                        </span>
                      </button>
                    ))}
                  </div>
                </div>

                {/* Modern Time Slot Pills WITH LOCKED/TAKEN LOGIC */}
                <div className={!pickupDate ? "opacity-50 pointer-events-none transition-opacity" : "transition-opacity"}>
                  <label className="font-semibold text-gray-900 text-sm mb-3 flex items-center gap-2">
                    <Clock size={16} className="text-gray-600" />
                    Pickup Time {(!pickupDate) && <span className="text-red-500 font-normal text-xs ml-2">(Select date first)</span>}
                  </label>
                  <div className="grid grid-cols-2 gap-3">
                    {TIME_SLOTS.map((slot) => {
                      const isBooked = bookedPickupSlots.includes(slot.id);
                      
                      return (
                        <button
                          key={`pickup-${slot.id}`}
                          onClick={() => setPickupTime(slot.id)}
                          disabled={isBooked}
                          className={`py-3 px-2 rounded-xl text-sm font-medium transition-all border-2 flex items-center justify-center gap-2 ${
                            isBooked 
                              ? 'bg-gray-100 border-gray-100 text-gray-400 cursor-not-allowed opacity-75'
                              : pickupTime === slot.id
                                ? 'bg-blue-600 border-blue-600 text-white shadow-md scale-[1.02]'
                                : 'bg-white border-gray-200 text-gray-700 hover:border-blue-300 hover:bg-blue-50'
                          }`}
                        >
                          {isBooked && <span className="w-2 h-2 rounded-full bg-red-400"></span>}
                          {isBooked ? "Booked" : slot.label}
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Address Input */}
                <div>
                  <label className="font-semibold text-gray-900 text-sm mb-2 flex items-center gap-2">
                    <MapPin size={16} className="text-gray-600" />
                    Pickup Address
                  </label>
                  <input
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    className="w-full border border-gray-300 rounded-xl px-4 py-3.5 text-sm font-medium text-gray-900 bg-gray-50 focus:bg-white focus:outline-none focus:border-blue-600 focus:ring-4 focus:ring-blue-600/10 transition-all"
                  />
                </div>
              </div>
            </div>

            {/* ====== DELIVERY DETAILS ====== */}
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8 flex flex-col">
              <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                  <div className="bg-green-100 rounded-xl w-10 h-10 flex items-center justify-center">
                    <PackageIcon className="text-green-600" size={20} />
                  </div>
                  <div>
                    <h2 className="text-xl font-bold text-gray-900">Delivery Details</h2>
                    <p className="text-gray-600 text-xs">
                      {selectedService 
                        ? `Minimum turnaround: ${minDeliveryDays} day(s)` 
                        : "When should we return your laundry?"}
                    </p>
                  </div>
                </div>

                {/* Updated Action Button based on service */}
                <button
                  onClick={handleFastestDelivery}
                  disabled={!pickupDate || !selectedService}
                  className="flex items-center gap-2 text-xs font-semibold text-blue-600 bg-blue-50 hover:bg-blue-100 px-3 py-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <CheckCircle2 size={14} />
                  <span className="hidden sm:inline">Fastest Available</span>
                </button>
              </div>

              <div className="space-y-6 flex-1">
                {/* Modern Date Slider */}
                <div className={!pickupDate || !selectedService ? "opacity-50 pointer-events-none transition-opacity" : "transition-opacity"}>
                  <label className="font-semibold text-gray-900 text-sm mb-3 flex items-center gap-2">
                    <Calendar size={16} className="text-gray-600" />
                    Delivery Date {(!pickupDate || !selectedService) && <span className="text-red-500 font-normal text-xs ml-2">(Select service & pickup first)</span>}
                  </label>
                  <div className="flex gap-3 overflow-x-auto pb-2 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
                    {deliveryDateOptions.map((dateObj) => (
                      <button
                        key={`delivery-${dateObj.value}`}
                        onClick={() => setDeliveryDate(dateObj.value)}
                        className={`flex-shrink-0 flex flex-col items-center justify-center w-20 h-24 rounded-2xl border-2 transition-all ${
                          deliveryDate === dateObj.value
                            ? 'bg-green-600 border-green-600 text-white shadow-md scale-[1.02]'
                            : 'bg-white border-gray-200 text-gray-700 hover:border-green-300 hover:bg-green-50'
                        }`}
                      >
                        <span className={`text-xs font-medium uppercase ${deliveryDate === dateObj.value ? 'text-green-100' : 'text-gray-500'}`}>
                          {dateObj.dayName}
                        </span>
                        <span className="text-2xl font-bold my-0.5">{dateObj.dayNum}</span>
                        <span className={`text-xs ${deliveryDate === dateObj.value ? 'text-green-100' : 'text-gray-500'}`}>
                          {dateObj.monthName}
                        </span>
                      </button>
                    ))}
                  </div>
                </div>

                {/* Modern Time Slot Pills */}
                <div className={!deliveryDate ? "opacity-50 pointer-events-none transition-opacity" : "transition-opacity"}>
                  <label className="font-semibold text-gray-900 text-sm mb-3 flex items-center gap-2">
                    <Clock size={16} className="text-gray-600" />
                    Delivery Time
                  </label>
                  <div className="grid grid-cols-2 gap-3">
                    {TIME_SLOTS.map((slot) => (
                      <button
                        key={`delivery-${slot.id}`}
                        onClick={() => setDeliveryTime(slot.id)}
                        className={`py-3 px-2 rounded-xl text-sm font-medium transition-all border-2 ${
                          deliveryTime === slot.id
                            ? 'bg-green-600 border-green-600 text-white shadow-md scale-[1.02]'
                            : 'bg-white border-gray-200 text-gray-700 hover:border-green-300 hover:bg-green-50'
                        }`}
                      >
                        {slot.label}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Special Instructions */}
            <div className="xl:col-span-2 bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Special Instructions</h2>
              <textarea
                value={specialInstructions}
                onChange={(e) => setSpecialInstructions(e.target.value)}
                placeholder="Add any special instructions for handling your laundry (e.g., fragrance preferences, stain removal notes, delicate items)"
                rows={4}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-900 bg-gray-50 focus:bg-white focus:outline-none focus:border-blue-600 focus:ring-4 focus:ring-blue-600/10 transition-all resize-none"
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
                    ? `Pickup: ${pickupDate} (${TIME_SLOTS.find(t => t.id === pickupTime)?.label})`
                    : "Complete the form to book your service"}
                </p>
                {weight && parseFloat(weight) > 0 && selectedService && (
                  <div className="flex items-baseline gap-3">
                    <span className="font-bold text-white text-3xl">
                      ₱{totalPrice.toFixed(0)}
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
                  disabled={!selectedService || !pickupDate || !pickupTime || !deliveryDate || !deliveryTime || !weight || parseFloat(weight) <= 0 || isSubmitting}
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

        {/* ====== NEW CUSTOM SUCCESS MODAL ====== */}
        {bookingSuccess?.show && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-8 animate-in fade-in zoom-in duration-200">
              <div className="flex flex-col items-center text-center">
                <div className="bg-green-100 w-20 h-20 rounded-full flex items-center justify-center mb-6">
                  <CheckCircle2 className="text-green-600" size={40} />
                </div>
                <h3 className="text-2xl font-bold text-gray-900 mb-2">Booking Confirmed!</h3>
                <p className="text-gray-600 mb-6 leading-relaxed">
                  Your laundry service has been successfully booked. We'll send you a confirmation email shortly.
                </p>
                
                <div className="bg-gray-50 rounded-xl p-4 w-full mb-8 text-left border border-gray-100">
                  <div className="flex justify-between items-center mb-3">
                    <span className="text-gray-500 text-sm font-medium">Order ID:</span>
                    <span className="font-mono text-gray-900 text-sm bg-gray-200 px-2 py-1 rounded-md">{bookingSuccess.orderId.substring(0,8)}...</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-gray-500 text-sm font-medium">Total Price:</span>
                    <span className="font-bold text-green-600 text-lg">₱{bookingSuccess.total.toFixed(0)}</span>
                  </div>
                </div>
                
                <button
                  onClick={() => {
                    setBookingSuccess(null);
                    navigate('/my-orders');
                  }}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3.5 rounded-xl transition-colors shadow-sm"
                >
                  View My Orders
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}