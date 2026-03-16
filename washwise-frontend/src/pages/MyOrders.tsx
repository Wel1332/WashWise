import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { 
  Calendar, 
  Package as PackageIcon, 
  MapPin, 
  ChevronDown, 
  ChevronUp, 
  RefreshCw, 
  X,
  LayoutDashboard,
  ShoppingCart,
  UserCircle,
  LogOut,
  Droplets,
  User as UserIcon
} from "lucide-react";
import { useAuthStore } from '../store/authStore';

type OrderStatus = "pending" | "picked-up" | "processing" | "out-for-delivery" | "delivered" | "cancelled";

interface Order {
  id: string;
  serviceType: string;
  serviceIcon: string;
  status: OrderStatus;
  pickupDate: string;
  deliveryDate: string;
  pickupAddress: string;
  items: number;
  total: string;
  specialInstructions?: string;
  trackingUpdates: {
    status: string;
    timestamp: string;
    description: string;
  }[];
}

const mockOrders: Order[] = [
  {
    id: "WW-2024-001",
    serviceType: "Wash & Fold",
    serviceIcon: "#007bff",
    status: "processing",
    pickupDate: "2024-03-15",
    deliveryDate: "2024-03-17",
    pickupAddress: "123 Main Street, Apt 4B",
    items: 12,
    total: "$18.00",
    specialInstructions: "Please use unscented detergent",
    trackingUpdates: [
      { status: "Order Placed", timestamp: "2024-03-14 09:30 AM", description: "Your order has been confirmed" },
      { status: "Picked Up", timestamp: "2024-03-15 10:15 AM", description: "Driver collected your laundry" },
      { status: "Processing", timestamp: "2024-03-15 02:45 PM", description: "Your items are being cleaned" },
    ],
  },
  {
    id: "WW-2024-002",
    serviceType: "Dry Cleaning",
    serviceIcon: "#9810FA",
    status: "out-for-delivery",
    pickupDate: "2024-03-13",
    deliveryDate: "2024-03-16",
    pickupAddress: "123 Main Street, Apt 4B",
    items: 3,
    total: "$26.97",
    trackingUpdates: [
      { status: "Order Placed", timestamp: "2024-03-12 02:20 PM", description: "Your order has been confirmed" },
      { status: "Picked Up", timestamp: "2024-03-13 11:30 AM", description: "Driver collected your items" },
      { status: "Processing", timestamp: "2024-03-14 09:00 AM", description: "Professional dry cleaning in progress" },
      { status: "Out for Delivery", timestamp: "2024-03-16 08:30 AM", description: "On the way to your location" },
    ],
  },
  {
    id: "WW-2024-003",
    serviceType: "Iron & Press",
    serviceIcon: "#00a63e",
    status: "delivered",
    pickupDate: "2024-03-10",
    deliveryDate: "2024-03-11",
    pickupAddress: "123 Main Street, Apt 4B",
    items: 8,
    total: "$28.00",
    trackingUpdates: [
      { status: "Order Placed", timestamp: "2024-03-09 03:15 PM", description: "Your order has been confirmed" },
      { status: "Picked Up", timestamp: "2024-03-10 01:00 PM", description: "Driver collected your items" },
      { status: "Processing", timestamp: "2024-03-10 04:30 PM", description: "Items being ironed and pressed" },
      { status: "Out for Delivery", timestamp: "2024-03-11 09:00 AM", description: "On the way to your location" },
      { status: "Delivered", timestamp: "2024-03-11 10:45 AM", description: "Successfully delivered" },
    ],
  },
];

const statusConfig: Record<OrderStatus, { label: string; color: string; bgColor: string }> = {
  "pending": { label: "Pending Pickup", color: "#6c757d", bgColor: "#f8f9fa" },
  "picked-up": { label: "Picked Up", color: "#007bff", bgColor: "#eff6ff" },
  "processing": { label: "Processing", color: "#FF6B35", bgColor: "#fff5f3" },
  "out-for-delivery": { label: "Out for Delivery", color: "#9810FA", bgColor: "#faf5ff" },
  "delivered": { label: "Delivered", color: "#00a63e", bgColor: "#f0fdf4" },
  "cancelled": { label: "Cancelled", color: "#dc3545", bgColor: "#fef2f2" },
};

export default function MyOrders() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [orders] = useState<Order[]>(mockOrders);
  const [expandedOrder, setExpandedOrder] = useState<string | null>(null);
  const [filterStatus, setFilterStatus] = useState<OrderStatus | "all">("all");

  const filteredOrders = filterStatus === "all" 
    ? orders 
    : orders.filter(order => order.status === filterStatus);

  const toggleOrderExpansion = (orderId: string) => {
    setExpandedOrder(expandedOrder === orderId ? null : orderId);
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
              onClick={() => navigate('/book-service')}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-gray-700 hover:bg-gray-100 transition-all"
            >
              <ShoppingCart size={20} />
              <span>Book Service</span>
            </button>

            <button
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium bg-blue-600 text-white transition-all"
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
            <h1 className="text-4xl font-bold text-gray-900 mb-2">My Orders</h1>
            <p className="text-lg text-gray-600">Track and manage your laundry orders</p>
          </div>

          {/* Filter Tabs */}
          <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 mb-6">
            <div className="flex flex-wrap gap-2">
              <button
                onClick={() => setFilterStatus("all")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "all"
                    ? "bg-blue-600 text-white shadow-md"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                All Orders ({orders.length})
              </button>
              <button
                onClick={() => setFilterStatus("processing")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "processing"
                    ? "bg-blue-600 text-white shadow-md"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Processing ({orders.filter(o => o.status === "processing").length})
              </button>
              <button
                onClick={() => setFilterStatus("out-for-delivery")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "out-for-delivery"
                    ? "bg-blue-600 text-white shadow-md"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Out for Delivery ({orders.filter(o => o.status === "out-for-delivery").length})
              </button>
              <button
                onClick={() => setFilterStatus("delivered")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "delivered"
                    ? "bg-blue-600 text-white shadow-md"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Delivered ({orders.filter(o => o.status === "delivered").length})
              </button>
            </div>
          </div>

          {/* Orders List */}
          {filteredOrders.length === 0 ? (
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-12 flex flex-col items-center justify-center min-h-[400px]">
              <div className="text-center">
                <div className="bg-gray-100 rounded-full w-20 h-20 flex items-center justify-center mx-auto mb-5">
                  <PackageIcon className="text-gray-400" size={40} />
                </div>
                <p className="text-lg font-semibold text-gray-900 mb-2">No orders found</p>
                <p className="text-sm text-gray-600 mb-6">
                  {filterStatus === "all" 
                    ? "You haven't placed any orders yet"
                    : `No orders with "${statusConfig[filterStatus as OrderStatus]?.label}" status`}
                </p>
                <button 
                  onClick={() => navigate('/book-service')}
                  className="bg-blue-600 text-white rounded-xl px-6 py-3 text-sm font-semibold hover:bg-blue-700 transition-colors"
                >
                  Book Your First Service
                </button>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              {filteredOrders.map((order) => {
                const statusInfo = statusConfig[order.status];
                const isExpanded = expandedOrder === order.id;

                return (
                  <div
                    key={order.id}
                    className="bg-white border border-gray-200 rounded-2xl shadow-sm overflow-hidden"
                  >
                    {/* Order Header */}
                    <div className="p-6">
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex items-center gap-4">
                          <div
                            className="rounded-xl w-14 h-14 flex items-center justify-center shrink-0"
                            style={{ backgroundColor: statusInfo.bgColor }}
                          >
                            <PackageIcon 
                              size={24} 
                              style={{ color: order.serviceIcon }}
                            />
                          </div>
                          <div>
                            <div className="flex items-center gap-3 mb-1">
                              <h3 className="font-semibold text-gray-900 text-lg">{order.serviceType}</h3>
                              <span
                                className="px-3 py-1 rounded-lg text-xs font-semibold"
                                style={{
                                  backgroundColor: statusInfo.bgColor,
                                  color: statusInfo.color,
                                }}
                              >
                                {statusInfo.label}
                              </span>
                            </div>
                            <p className="text-gray-600 text-sm">Order ID: {order.id}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="font-bold text-gray-900 text-xl mb-0.5">{order.total}</p>
                          <p className="text-gray-600 text-xs">{order.items} items</p>
                        </div>
                      </div>

                      {/* Quick Info */}
                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <div className="flex items-center gap-2">
                          <Calendar size={16} className="text-gray-500" />
                          <div>
                            <p className="font-semibold text-gray-900 text-xs">Pickup</p>
                            <p className="text-gray-600 text-xs">{order.pickupDate}</p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          <PackageIcon size={16} className="text-gray-500" />
                          <div>
                            <p className="font-semibold text-gray-900 text-xs">Delivery</p>
                            <p className="text-gray-600 text-xs">{order.deliveryDate}</p>
                          </div>
                        </div>
                      </div>

                      {/* Actions */}
                      <div className="flex gap-2">
                        <button
                          onClick={() => toggleOrderExpansion(order.id)}
                          className="flex-1 bg-blue-600 text-white rounded-xl px-4 py-2.5 text-sm font-medium hover:bg-blue-700 transition-colors flex items-center justify-center gap-2"
                        >
                          {isExpanded ? "Hide Details" : "View Details"}
                          {isExpanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                        </button>
                        {order.status === "delivered" && (
                          <button className="border border-gray-300 text-gray-700 rounded-xl px-4 py-2.5 text-sm font-medium hover:bg-gray-50 transition-colors flex items-center gap-2">
                            <RefreshCw size={16} />
                            Reorder
                          </button>
                        )}
                        {(order.status === "pending" || order.status === "picked-up") && (
                          <button className="border border-red-600 text-red-600 rounded-xl px-4 py-2.5 text-sm font-medium hover:bg-red-50 transition-colors flex items-center gap-2">
                            <X size={16} />
                            Cancel
                          </button>
                        )}
                      </div>
                    </div>

                    {/* Expanded Details */}
                    {isExpanded && (
                      <div className="border-t border-gray-200 bg-gray-50 p-6">
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                          {/* Order Details */}
                          <div>
                            <h4 className="font-semibold text-gray-900 text-base mb-4">Order Details</h4>
                            <div className="space-y-3">
                              <div className="flex items-start gap-2">
                                <MapPin size={16} className="text-gray-500 mt-0.5 shrink-0" />
                                <div>
                                  <p className="font-semibold text-gray-900 text-sm">Address</p>
                                  <p className="text-gray-600 text-sm">{order.pickupAddress}</p>
                                </div>
                              </div>
                              {order.specialInstructions && (
                                <div className="bg-white border border-gray-200 rounded-xl p-3">
                                  <p className="font-semibold text-gray-900 text-xs mb-1">Special Instructions</p>
                                  <p className="text-gray-600 text-xs">{order.specialInstructions}</p>
                                </div>
                              )}
                            </div>
                          </div>

                          {/* Tracking Timeline */}
                          <div>
                            <h4 className="font-semibold text-gray-900 text-base mb-4">Tracking History</h4>
                            <div className="space-y-4">
                              {order.trackingUpdates.map((update, index) => (
                                <div key={index} className="flex gap-3">
                                  <div className="flex flex-col items-center">
                                    <div
                                      className="rounded-full w-3 h-3 shrink-0"
                                      style={{
                                        backgroundColor: index === order.trackingUpdates.length - 1 ? statusInfo.color : "#dee2e6",
                                      }}
                                    />
                                    {index < order.trackingUpdates.length - 1 && (
                                      <div className="w-0.5 flex-1 bg-gray-300 my-1" style={{ minHeight: "20px" }} />
                                    )}
                                  </div>
                                  <div className="flex-1 pb-2">
                                    <p className="font-semibold text-gray-900 text-sm">{update.status}</p>
                                    <p className="text-gray-600 text-xs mb-0.5">{update.timestamp}</p>
                                    <p className="text-gray-600 text-xs">{update.description}</p>
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}