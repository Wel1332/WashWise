import { useState, useEffect } from 'react';
import { Users, Clock, CheckCircle, AlertCircle, Loader } from 'lucide-react';
import { ordersAPI } from '../services/api';

interface Order {
  id: string;
  serviceName: string;
  email: string;
  status: string;
  totalPrice: number;
  location: string;
  scheduledDate: string;
}

export default function StaffDashboard() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'ALL'>('ALL');

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const { data } = await ordersAPI.getAllOrders();
      setOrders(data.data);
    } catch (err) {
      console.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateOrderStatus = async (orderId: string, newStatus: string) => {
    try {
      await ordersAPI.updateOrder(orderId, { status: newStatus });
      alert('Order updated successfully');
      fetchOrders();
    } catch (err) {
      alert('Failed to update order');
    }
  };

  const filteredOrders = filter === 'ALL' 
    ? orders 
    : orders.filter(o => o.status === filter);

  const stats = {
    total: orders.length,
    pending: orders.filter(o => o.status === 'PENDING').length,
    confirmed: orders.filter(o => o.status === 'CONFIRMED').length,
    completed: orders.filter(o => o.status === 'COMPLETED').length,
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Loader size={48} className="animate-spin text-blue-600" />
      </div>
    );
  }

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 py-16">
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Staff Dashboard</h1>
          <p className="text-gray-600 text-lg">Manage and process customer orders</p>
        </div>

        {/* STATS */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
          <div className="bg-blue-50 rounded-xl border border-blue-200 p-6">
            <p className="text-gray-600 text-sm font-semibold">Total Orders</p>
            <p className="text-4xl font-bold text-blue-600 mt-2">{stats.total}</p>
          </div>
          <div className="bg-yellow-50 rounded-xl border border-yellow-200 p-6">
            <p className="text-gray-600 text-sm font-semibold">Pending</p>
            <p className="text-4xl font-bold text-yellow-600 mt-2">{stats.pending}</p>
          </div>
          <div className="bg-purple-50 rounded-xl border border-purple-200 p-6">
            <p className="text-gray-600 text-sm font-semibold">Confirmed</p>
            <p className="text-4xl font-bold text-purple-600 mt-2">{stats.confirmed}</p>
          </div>
          <div className="bg-green-50 rounded-xl border border-green-200 p-6">
            <p className="text-gray-600 text-sm font-semibold">Completed</p>
            <p className="text-4xl font-bold text-green-600 mt-2">{stats.completed}</p>
          </div>
        </div>

        {/* FILTERS */}
        <div className="flex gap-3 mb-8">
          {['ALL', 'PENDING', 'CONFIRMED', 'COMPLETED'].map((status) => (
            <button
              key={status}
              onClick={() => setFilter(status as any)}
              className={`px-6 py-2 rounded-lg font-semibold transition ${
                filter === status
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              {status}
            </button>
          ))}
        </div>

        {/* ORDERS TABLE */}
        <div className="bg-gray-50 rounded-xl border border-gray-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Customer</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Service</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Location</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Price</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Scheduled</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-4 text-left font-semibold text-gray-900">Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredOrders.map((order) => (
                  <tr key={order.id} className="border-b border-gray-200 hover:bg-white transition">
                    <td className="px-6 py-4 text-gray-900 font-medium">{order.email}</td>
                    <td className="px-6 py-4 text-gray-600">{order.serviceName}</td>
                    <td className="px-6 py-4 text-gray-600">{order.location}</td>
                    <td className="px-6 py-4 text-green-600 font-semibold">${order.totalPrice}</td>
                    <td className="px-6 py-4 text-gray-600 text-sm">
                      {new Date(order.scheduledDate).toLocaleString()}
                    </td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-sm font-semibold inline-block ${
                        order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                        order.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-800' :
                        order.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {order.status}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <select
                        value={order.status}
                        onChange={(e) => handleUpdateOrderStatus(order.id, e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-lg font-medium text-sm focus:outline-none focus:ring-2 focus:ring-blue-600"
                      >
                        <option value="PENDING">PENDING</option>
                        <option value="CONFIRMED">CONFIRMED</option>
                        <option value="COMPLETED">COMPLETED</option>
                        <option value="CANCELLED">CANCELLED</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}