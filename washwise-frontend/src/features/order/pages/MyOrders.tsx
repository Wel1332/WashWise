import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { 
  Calendar, 
  Package as PackageIcon, 
  MapPin, 
  ChevronDown, 
  ChevronUp, 
  RefreshCw, 
  X,
  Loader,
  AlertCircle,
  CheckCircle
} from "lucide-react";
import { ordersAPI } from '../../../shared/services/api';
import Sidebar from '../../../shared/components/Sidebar';

// Status configuration matched to your backend
const statusConfig: Record<string, { label: string; color: string; bgColor: string }> = {
  "PENDING": { label: "Pending Pickup", color: "#6c757d", bgColor: "#f8f9fa" },
  "RECEIVED": { label: "Received", color: "#007bff", bgColor: "#eff6ff" },
  "WASHING": { label: "Washing", color: "#FF6B35", bgColor: "#fff5f3" },
  "DRYING": { label: "Drying", color: "#FF6B35", bgColor: "#fff5f3" },
  "READY": { label: "Ready for Delivery", color: "#9810FA", bgColor: "#faf5ff" },
  "COMPLETED": { label: "Completed", color: "#00a63e", bgColor: "#f0fdf4" },
  "CANCELLED": { label: "Cancelled", color: "#dc3545", bgColor: "#fef2f2" },
};

// Dynamic colors based on service type
const getServiceStyle = (name: string) => {
  if (name?.includes('Wash')) return { color: "#007bff", bgColor: "#eff6ff" };
  if (name?.includes('Dry')) return { color: "#9810FA", bgColor: "#faf5ff" };
  return { color: "#FF6B35", bgColor: "#fff5f3" }; // Premium/Default
};

export default function MyOrders() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [expandedOrder, setExpandedOrder] = useState<string | null>(null);
  const [filterStatus, setFilterStatus] = useState<string>("ALL");

  // UI Notification & Modal States
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [orderToCancel, setOrderToCancel] = useState<string | null>(null);
  const [isCancelling, setIsCancelling] = useState(false);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const { data } = await ordersAPI.getMyOrders();
      // Sort newest first
      const sortedOrders = (data.data || []).sort((a: any, b: any) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
      setOrders(sortedOrders);
    } catch (error) {
      console.error("Failed to fetch orders:", error);
    } finally {
      setLoading(false);
    }
  };

  // Custom modal confirmation handler instead of window.confirm
  const confirmCancelOrder = async () => {
    if (!orderToCancel) return;
    
    try {
      setIsCancelling(true);
      setError(null);
      setSuccess(null);
      
      await ordersAPI.cancelOrder(orderToCancel);
      
      // Remove the deleted order from the screen immediately
      setOrders(prevOrders => prevOrders.filter(o => o.id !== orderToCancel));
      
      // Show UI success banner
      setSuccess("Order cancelled and deleted successfully.");
      
      // Auto-hide the success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
      
    } catch (err: any) {
      console.error("Failed to cancel order:", err);
      // Show UI error banner
      setError(err.response?.data?.message || "Failed to cancel order.");
    } finally {
      setIsCancelling(false);
      setOrderToCancel(null); // Close the modal
    }
  };

  const filteredOrders = filterStatus === "ALL" 
    ? orders 
    : filterStatus === "ACTIVE"
    ? orders.filter(o => !["COMPLETED", "CANCELLED"].includes(o.status))
    : orders.filter(o => o.status === filterStatus);

  const toggleOrderExpansion = (orderId: string) => {
    setExpandedOrder(expandedOrder === orderId ? null : orderId);
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  // Generate a dynamic tracking timeline based on current status
  const generateTimeline = (order: any) => {
    const updates = [
      { status: "Order Placed", timestamp: formatDate(order.createdAt), description: "Your order has been confirmed by the system." }
    ];
    if (order.status !== 'PENDING') {
      updates.push({ status: "Received", timestamp: "Updated", description: "Our team has collected your items." });
    }
    if (['WASHING', 'DRYING', 'READY', 'COMPLETED'].includes(order.status)) {
      updates.push({ status: "Processing", timestamp: "Updated", description: "Cleaning process is underway." });
    }
    if (['READY', 'COMPLETED'].includes(order.status)) {
      updates.push({ status: "Ready", timestamp: "Updated", description: "Items are clean and ready." });
    }
    if (order.status === 'COMPLETED') {
      updates.push({ status: "Completed", timestamp: formatDate(order.completedDate || order.updatedAt), description: "Order successfully completed." });
    }
    return updates;
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <Sidebar userRole="CUSTOMER" activePage="my-orders" />

      {/* Main Content */}
      <main className="flex-1 overflow-auto relative">
        
        {/* Cancel Confirmation Modal */}
        {orderToCancel && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
            <div className="bg-white rounded-3xl p-8 max-w-md w-full shadow-2xl animate-in fade-in zoom-in duration-200">
              <div className="flex items-center gap-4 mb-4">
                <div className="bg-red-100 p-3 rounded-full text-red-600">
                  <AlertCircle size={28} />
                </div>
                <h3 className="text-2xl font-bold text-gray-900">Cancel Order?</h3>
              </div>
              <p className="text-gray-600 mb-8 leading-relaxed">
                Are you sure you want to cancel and completely delete this order? This action cannot be undone.
              </p>
              <div className="flex justify-end gap-3">
                <button
                  onClick={() => setOrderToCancel(null)}
                  className="px-5 py-2.5 text-gray-700 hover:bg-gray-100 rounded-xl font-semibold transition-colors"
                  disabled={isCancelling}
                >
                  No, Keep It
                </button>
                <button
                  onClick={confirmCancelOrder}
                  className="px-5 py-2.5 bg-red-600 hover:bg-red-700 text-white rounded-xl font-semibold transition-colors flex items-center gap-2 shadow-md hover:shadow-lg"
                  disabled={isCancelling}
                >
                  {isCancelling ? <Loader size={18} className="animate-spin" /> : null}
                  Yes, Cancel Order
                </button>
              </div>
            </div>
          </div>
        )}

        <div className="p-12">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">My Orders</h1>
            <p className="text-lg text-gray-600">Track and manage your laundry orders</p>
          </div>

          {/* Success Message Banner */}
          {success && (
            <div className="mb-6 bg-green-50 border border-green-200 rounded-2xl p-4 flex items-start gap-3 transition-all animate-in fade-in slide-in-from-top-4">
              <CheckCircle className="text-green-600 flex-shrink-0" size={20} />
              <div>
                <p className="text-green-900 font-medium">{success}</p>
              </div>
              <button
                onClick={() => setSuccess(null)}
                className="ml-auto text-green-600 hover:text-green-700"
              >
                ✕
              </button>
            </div>
          )}

          {/* Error Message Banner */}
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 rounded-2xl p-4 flex items-start gap-3 transition-all animate-in fade-in slide-in-from-top-4">
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

          {/* Filter Tabs */}
          <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 mb-6">
            <div className="flex flex-wrap gap-2">
              <button
                onClick={() => setFilterStatus("ALL")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "ALL" ? "bg-blue-600 text-white shadow-md" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                All Orders ({orders.length})
              </button>
              <button
                onClick={() => setFilterStatus("ACTIVE")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "ACTIVE" ? "bg-blue-600 text-white shadow-md" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Active ({orders.filter(o => !["COMPLETED", "CANCELLED"].includes(o.status)).length})
              </button>
              <button
                onClick={() => setFilterStatus("COMPLETED")}
                className={`px-5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  filterStatus === "COMPLETED" ? "bg-blue-600 text-white shadow-md" : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Completed ({orders.filter(o => o.status === "COMPLETED").length})
              </button>
            </div>
          </div>

          {/* Orders List */}
          {loading ? (
            <div className="flex justify-center items-center py-20">
              <Loader className="animate-spin text-blue-600" size={48} />
            </div>
          ) : filteredOrders.length === 0 ? (
            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-12 flex flex-col items-center justify-center min-h-[400px]">
              <div className="text-center">
                <div className="bg-gray-100 rounded-full w-20 h-20 flex items-center justify-center mx-auto mb-5">
                  <PackageIcon className="text-gray-400" size={40} />
                </div>
                <p className="text-lg font-semibold text-gray-900 mb-2">No orders found</p>
                <p className="text-sm text-gray-600 mb-6">
                  {filterStatus === "ALL" 
                    ? "You haven't placed any orders yet"
                    : `No orders in the current view`}
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
                const statusInfo = statusConfig[order.status] || { label: order.status, color: "#6c757d", bgColor: "#f8f9fa" };
                const isExpanded = expandedOrder === order.id;
                const serviceStyle = getServiceStyle(order.service?.name);

                return (
                  <div key={order.id} className="bg-white border border-gray-200 rounded-2xl shadow-sm overflow-hidden">
                    {/* Order Header */}
                    <div className="p-6">
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex items-center gap-4">
                          <div
                            className="rounded-xl w-14 h-14 flex items-center justify-center shrink-0"
                            style={{ backgroundColor: serviceStyle.bgColor }}
                          >
                            <PackageIcon size={24} style={{ color: serviceStyle.color }} />
                          </div>
                          <div>
                            <div className="flex items-center gap-3 mb-1">
                              <h3 className="font-semibold text-gray-900 text-lg">{order.service?.name || 'Laundry Service'}</h3>
                              <span
                                className="px-3 py-1 rounded-lg text-xs font-semibold"
                                style={{ backgroundColor: statusInfo.bgColor, color: statusInfo.color }}
                              >
                                {statusInfo.label}
                              </span>
                            </div>
                            <p className="text-gray-600 text-sm">Order: WW-2026-{String(order.id).slice(0, 5)}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="font-bold text-gray-900 text-xl mb-0.5">₱{parseFloat(order.totalPrice || 0).toFixed(2)}</p>
                          <p className="text-gray-600 text-xs">{order.weightKg ? `${order.weightKg} kg` : 'Standard'}</p>
                        </div>
                      </div>

                      {/* Quick Info */}
                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <div className="flex items-center gap-2">
                          <Calendar size={16} className="text-gray-500" />
                          <div>
                            <p className="font-semibold text-gray-900 text-xs">Scheduled Date</p>
                            <p className="text-gray-600 text-xs">{formatDate(order.scheduledDate)}</p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          <PackageIcon size={16} className="text-gray-500" />
                          <div>
                            <p className="font-semibold text-gray-900 text-xs">Last Updated</p>
                            <p className="text-gray-600 text-xs">{formatDate(order.updatedAt)}</p>
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
                        {order.status === "COMPLETED" && (
                          <button onClick={() => navigate('/book-service')} className="border border-gray-300 text-gray-700 rounded-xl px-4 py-2.5 text-sm font-medium hover:bg-gray-50 transition-colors flex items-center gap-2">
                            <RefreshCw size={16} />
                            Reorder
                          </button>
                        )}
                        {order.status === "PENDING" && (
                          <button 
                            onClick={() => setOrderToCancel(order.id)}
                            className="border border-red-600 text-red-600 rounded-xl px-4 py-2.5 text-sm font-medium hover:bg-red-50 transition-colors flex items-center gap-2"
                          >
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
                                  <p className="font-semibold text-gray-900 text-sm">Location</p>
                                  <p className="text-gray-600 text-sm">{order.location || 'N/A'}</p>
                                </div>
                              </div>
                              {order.notes && (
                                <div className="bg-white border border-gray-200 rounded-xl p-3">
                                  <p className="font-semibold text-gray-900 text-xs mb-1">Notes / Instructions</p>
                                  <p className="text-gray-600 text-xs whitespace-pre-line">{order.notes}</p>
                                </div>
                              )}
                            </div>
                          </div>

                          {/* Tracking Timeline */}
                          <div>
                            <h4 className="font-semibold text-gray-900 text-base mb-4">Tracking History</h4>
                            <div className="space-y-4">
                              {generateTimeline(order).map((update, index, arr) => (
                                <div key={index} className="flex gap-3">
                                  <div className="flex flex-col items-center">
                                    <div
                                      className="rounded-full w-3 h-3 shrink-0"
                                      style={{ backgroundColor: index === arr.length - 1 ? statusInfo.color : "#dee2e6" }}
                                    />
                                    {index < arr.length - 1 && (
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