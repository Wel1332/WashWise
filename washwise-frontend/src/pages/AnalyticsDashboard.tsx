import { useState, useEffect } from 'react';
import { BarChart3, TrendingUp, Users, DollarSign, ShoppingCart, Loader } from 'lucide-react';
import { ordersAPI } from '../services/api';

interface Stats {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  averageOrderValue: number;
  pendingOrders: number;
  completedOrders: number;
}

export default function AnalyticsDashboard() {
  const [stats, setStats] = useState<Stats>({
    totalRevenue: 0,
    totalOrders: 0,
    totalCustomers: 0,
    averageOrderValue: 0,
    pendingOrders: 0,
    completedOrders: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const { data } = await ordersAPI.getAllOrders();
      const orders = data.data;

      const completedOrders = orders.filter((o: any) => o.status === 'COMPLETED');
      const totalRevenue = completedOrders.reduce((sum: number, o: any) => sum + o.totalPrice, 0);
      const uniqueCustomers = new Set(orders.map((o: any) => o.email)).size;
      const pendingOrders = orders.filter((o: any) => o.status === 'PENDING').length;

      setStats({
        totalRevenue: Math.round(totalRevenue * 100) / 100,
        totalOrders: orders.length,
        totalCustomers: uniqueCustomers,
        averageOrderValue: orders.length > 0 ? Math.round((totalRevenue / orders.length) * 100) / 100 : 0,
        pendingOrders,
        completedOrders: completedOrders.length,
      });
    } catch (err) {
      console.error('Failed to load analytics');
    } finally {
      setLoading(false);
    }
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
        {/* HEADER */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4 flex items-center gap-3">
            <div className="bg-blue-100 p-3 rounded-lg">
              <BarChart3 size={36} className="text-blue-600" />
            </div>
            Analytics Dashboard
          </h1>
          <p className="text-gray-600 text-lg">Business performance metrics and insights</p>
        </div>

        {/* STATS GRID */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {/* Total Revenue */}
          <div className="bg-gradient-to-br from-green-50 to-green-100 rounded-xl border-2 border-green-200 p-8 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-bold uppercase tracking-wide">Total Revenue</p>
                <p className="text-4xl font-bold text-green-600 mt-3">${stats.totalRevenue.toFixed(2)}</p>
              </div>
              <div className="bg-green-200 p-4 rounded-full">
                <DollarSign size={36} className="text-green-600" />
              </div>
            </div>
          </div>

          {/* Total Orders */}
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl border-2 border-blue-200 p-8 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-bold uppercase tracking-wide">Total Orders</p>
                <p className="text-4xl font-bold text-blue-600 mt-3">{stats.totalOrders}</p>
              </div>
              <div className="bg-blue-200 p-4 rounded-full">
                <ShoppingCart size={36} className="text-blue-600" />
              </div>
            </div>
          </div>

          {/* Total Customers */}
          <div className="bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl border-2 border-purple-200 p-8 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-bold uppercase tracking-wide">Total Customers</p>
                <p className="text-4xl font-bold text-purple-600 mt-3">{stats.totalCustomers}</p>
              </div>
              <div className="bg-purple-200 p-4 rounded-full">
                <Users size={36} className="text-purple-600" />
              </div>
            </div>
          </div>

          {/* Average Order Value */}
          <div className="bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl border-2 border-orange-200 p-8 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-bold uppercase tracking-wide">Avg Order Value</p>
                <p className="text-4xl font-bold text-orange-600 mt-3">${stats.averageOrderValue.toFixed(2)}</p>
              </div>
              <div className="bg-orange-200 p-4 rounded-full">
                <TrendingUp size={36} className="text-orange-600" />
              </div>
            </div>
          </div>

          {/* Pending Orders */}
          <div className="bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl border-2 border-yellow-200 p-8 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-bold uppercase tracking-wide">Pending Orders</p>
                <p className="text-4xl font-bold text-yellow-600 mt-3">{stats.pendingOrders}</p>
              </div>
              <div className="bg-yellow-200 p-4 rounded-full">
                <ShoppingCart size={36} className="text-yellow-600" />
              </div>
            </div>
          </div>

          {/* Completed Orders */}
          <div className="bg-gradient-to-br from-teal-50 to-teal-100 rounded-xl border-2 border-teal-200 p-8 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-bold uppercase tracking-wide">Completed Orders</p>
                <p className="text-4xl font-bold text-teal-600 mt-3">{stats.completedOrders}</p>
              </div>
              <div className="bg-teal-200 p-4 rounded-full">
                <TrendingUp size={36} className="text-teal-600" />
              </div>
            </div>
          </div>
        </div>

        {/* PERFORMANCE METRICS */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Completion Rate */}
          <div className="bg-gray-50 rounded-xl border border-gray-200 p-8">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Completion Rate</h3>
            <div className="flex items-center">
              <div className="text-4xl font-bold text-green-600 mr-4">
                {stats.totalOrders > 0 ? Math.round((stats.completedOrders / stats.totalOrders) * 100) : 0}%
              </div>
              <div className="flex-grow">
                <div className="w-full bg-gray-300 rounded-full h-4">
                  <div
                    className="bg-gradient-to-r from-green-500 to-green-600 h-4 rounded-full transition-all"
                    style={{
                      width: `${stats.totalOrders > 0 ? (stats.completedOrders / stats.totalOrders) * 100 : 0}%`,
                    }}
                  ></div>
                </div>
              </div>
            </div>
          </div>

          {/* Revenue Growth */}
          <div className="bg-gray-50 rounded-xl border border-gray-200 p-8">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Revenue Status</h3>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-gray-700 font-medium">Monthly Revenue</span>
                <span className="text-green-600 font-bold text-xl">${stats.totalRevenue.toFixed(2)}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-700 font-medium">Orders Processed</span>
                <span className="text-blue-600 font-bold text-xl">{stats.completedOrders}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}