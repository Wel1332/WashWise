import { useState, useEffect } from 'react';
import { ShoppingCart, MapPin, Calendar, DollarSign, Trash2, AlertCircle, Loader, CheckCircle, Clock, XCircle } from 'lucide-react';
import { ordersAPI } from '../services/api';
import OrderTracking from '../components/OrderTracking'; // <-- Added import here

interface Order {
  id: string;
  serviceName: string;
  status: string;
  totalPrice: number;
  location: string;
  scheduledDate: string;
  notes: string;
}

export default function MyOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const { data } = await ordersAPI.getMyOrders();
      setOrders(data.data);
    } catch (err) { // <-- Removed the explicit 'any' to keep ESLint happy
      console.error('Failed to fetch orders:', err);
      setError('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelOrder = async (orderId: string) => {
    if (confirm('Are you sure you want to cancel this order?')) {
      try {
        await ordersAPI.cancelOrder(orderId);
        alert('Order cancelled successfully');
        fetchOrders();
      } catch (err) {
        console.error('Failed to cancel order:', err);
        alert('Failed to cancel order');
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <Loader size={48} className="animate-spin mx-auto text-blue-600 mb-4" />
          <p className="text-xl text-gray-600">Loading your orders...</p>
        </div>
      </div>
    );
  }

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'PENDING': return { bg: 'bg-yellow-50', border: 'border-yellow-200', text: 'text-yellow-700', icon: 'text-yellow-600' };
      case 'CONFIRMED': return { bg: 'bg-blue-50', border: 'border-blue-200', text: 'text-blue-700', icon: 'text-blue-600' };
      case 'COMPLETED': return { bg: 'bg-green-50', border: 'border-green-200', text: 'text-green-700', icon: 'text-green-600' };
      case 'CANCELLED': return { bg: 'bg-red-50', border: 'border-red-200', text: 'text-red-700', icon: 'text-red-600' };
      default: return { bg: 'bg-gray-50', border: 'border-gray-200', text: 'text-gray-700', icon: 'text-gray-600' };
    }
  };

  const getStatusIcon = (status: string) => {
    switch(status) {
      case 'PENDING': return <Clock size={20} />;
      case 'CONFIRMED': return <CheckCircle size={20} />;
      case 'COMPLETED': return <CheckCircle size={20} />;
      case 'CANCELLED': return <XCircle size={20} />;
      default: return <ShoppingCart size={20} />;
    }
  };

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 py-16">
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4 flex items-center gap-3">
            <div className="bg-blue-100 p-3 rounded-lg">
              <ShoppingCart size={36} className="text-blue-600" />
            </div>
            My Orders
          </h1>
          <p className="text-gray-600 text-lg">Track and manage your laundry orders</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-700 p-4 rounded-lg mb-8 flex items-center gap-3 border border-red-200">
            <AlertCircle size={20} />
            <span className="font-medium">{error}</span>
          </div>
        )}

        {orders.length === 0 ? (
          <div className="bg-gray-50 p-12 rounded-xl text-center border border-gray-200">
            <ShoppingCart size={48} className="mx-auto text-gray-400 mb-4" />
            <p className="text-gray-900 text-lg font-semibold mb-2">No orders yet</p>
            <p className="text-gray-600">Start by browsing our services and booking your first order!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6">
            {orders.map((order) => {
              const statusColor = getStatusColor(order.status);
              return (
                <div key={order.id} className={`rounded-xl border-2 ${statusColor.border} ${statusColor.bg} p-6 hover:shadow-lg transition`}>
                  <div className="flex justify-between items-start mb-6">
                    <div className="flex-1">
                      <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2 mb-2">
                        <ShoppingCart size={24} className="text-blue-600" />
                        {order.serviceName}
                      </h3>
                      <p className="text-gray-600 flex items-center gap-2">
                        <MapPin size={18} className="text-gray-500" />
                        {order.location}
                      </p>
                    </div>
                    <div className="text-right">
                      <div className={`px-4 py-2 rounded-lg text-white font-semibold flex items-center justify-center gap-2 mb-3 ${
                        order.status === 'PENDING' ? 'bg-yellow-500' :
                        order.status === 'CONFIRMED' ? 'bg-blue-500' :
                        order.status === 'COMPLETED' ? 'bg-green-500' :
                        'bg-red-500'
                      }`}>
                        {getStatusIcon(order.status)}
                        {order.status}
                      </div>
                      <p className="text-2xl font-bold text-gray-900 flex items-center justify-end gap-1">
                        <DollarSign size={24} className="text-green-600" />
                        {order.totalPrice}
                      </p>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6 pb-6 border-b border-gray-300">
                    <div>
                      <p className="text-gray-600 text-sm font-semibold flex items-center gap-2 mb-1">
                        <Calendar size={16} /> Scheduled Pickup
                      </p>
                      <p className="text-gray-900 font-medium">{new Date(order.scheduledDate).toLocaleString()}</p>
                    </div>
                    <div>
                      <p className="text-gray-600 text-sm font-semibold">Order Status</p>
                      <p className={`font-semibold flex items-center gap-2 ${statusColor.text}`}>
                        {getStatusIcon(order.status)}
                        {order.status}
                      </p>
                    </div>
                  </div>

                  {/* NEW ORDER TRACKING COMPONENT */}
                  <div className="mt-6 mb-6">
                    <OrderTracking 
                      currentStatus={order.status}
                      scheduledDate={order.scheduledDate}
                      location={order.location}
                    />
                  </div>

                  {order.notes && (
                    <div className="mb-6 bg-white p-4 rounded-lg border border-gray-200">
                      <p className="text-gray-600 text-sm font-semibold mb-2">Special Instructions</p>
                      <p className="text-gray-900">{order.notes}</p>
                    </div>
                  )}

                  {order.status === 'PENDING' && (
                    <button
                      onClick={() => handleCancelOrder(order.id)}
                      className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg font-semibold transition flex items-center gap-2"
                    >
                      <Trash2 size={18} />
                      Cancel Order
                    </button>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}