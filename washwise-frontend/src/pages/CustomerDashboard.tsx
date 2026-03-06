import { useState, useEffect } from 'react';
import { BarChart3, ShoppingCart, DollarSign, Clock, TrendingUp } from 'lucide-react';
import { ordersAPI } from '../services/api';
import { useAuthStore } from '../store/authStore';

interface Order {
  id: string;
  serviceName: string;
  status: string;
  totalPrice: number;
  location: string;
  scheduledDate: string;
}

export default function CustomerDashboard() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuthStore();

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const { data } = await ordersAPI.getMyOrders();
      setOrders(data.data);
    } catch (err) {
      console.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const stats = {
    totalOrders: orders.length,
    totalSpent: orders.reduce((sum, order) => sum + order.totalPrice, 0),
    pendingOrders: orders.filter(o => o.status === 'PENDING').length,
    completedOrders: orders.filter(o => o.status === 'COMPLETED').length,
  };

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 py-16">
        {/* HEADER */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Welcome back, {user?.fullName}!
          </h1>
          <p className="text-gray-600 text-lg">Here's your laundry service overview</p>
        </div>

        {/* STATS CARDS */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
          <div className="bg-blue-50 rounded-xl border border-blue-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-semibold">Total Orders</p>
                <p className="text-4xl font-bold text-gray-900 mt-2">{stats.totalOrders}</p>
              </div>
              <div className="bg-blue-100 p-4 rounded-lg">
                <ShoppingCart size={32} className="text-blue-600" />
              </div>
            </div>
          </div>

          <div className="bg-green-50 rounded-xl border border-green-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-semibold">Total Spent</p>
                <p className="text-4xl font-bold text-green-600 mt-2">${stats.totalSpent.toFixed(2)}</p>
              </div>
              <div className="bg-green-100 p-4 rounded-lg">
                <DollarSign size={32} className="text-green-600" />
              </div>
            </div>
          </div>

          <div className="bg-yellow-50 rounded-xl border border-yellow-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-semibold">Pending Orders</p>
                <p className="text-4xl font-bold text-yellow-600 mt-2">{stats.pendingOrders}</p>
              </div>
              <div className="bg-yellow-100 p-4 rounded-lg">
                <Clock size={32} className="text-yellow-600" />
              </div>
            </div>
          </div>

          <div className="bg-purple-50 rounded-xl border border-purple-200 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-semibold">Completed Orders</p>
                <p className="text-4xl font-bold text-purple-600 mt-2">{stats.completedOrders}</p>
              </div>
              <div className="bg-purple-100 p-4 rounded-lg">
                <TrendingUp size={32} className="text-purple-600" />
              </div>
            </div>
          </div>
        </div>

        {/* RECENT ORDERS */}
        <div className="bg-gray-50 rounded-xl border border-gray-200 p-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-3">
            <BarChart3 size={28} className="text-blue-600" />
            Recent Orders
          </h2>

          {loading ? (
            <p className="text-gray-600">Loading orders...</p>
          ) : orders.length === 0 ? (
            <p className="text-gray-600">No orders yet. Start by booking a service!</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-300">
                    <th className="text-left py-3 px-4 font-semibold text-gray-900">Service</th>
                    <th className="text-left py-3 px-4 font-semibold text-gray-900">Location</th>
                    <th className="text-left py-3 px-4 font-semibold text-gray-900">Price</th>
                    <th className="text-left py-3 px-4 font-semibold text-gray-900">Status</th>
                    <th className="text-left py-3 px-4 font-semibold text-gray-900">Date</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id} className="border-b border-gray-200 hover:bg-white transition">
                      <td className="py-4 px-4 text-gray-900 font-medium">{order.serviceName}</td>
                      <td className="py-4 px-4 text-gray-600">{order.location}</td>
                      <td className="py-4 px-4 text-green-600 font-semibold">${order.totalPrice}</td>
                      <td className="py-4 px-4">
                        <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                          order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                          order.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-800' :
                          order.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                          'bg-red-100 text-red-800'
                        }`}>
                          {order.status}
                        </span>
                      </td>
                      <td className="py-4 px-4 text-gray-600 text-sm">
                        {new Date(order.scheduledDate).toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}