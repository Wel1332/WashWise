import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Package, 
  Users, 
  TrendingUp, 
  Clock,
  ChevronRight,
  PackageCheck,
  Loader,
  Wind,
  CheckCircle2,
  Circle
} from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { ordersAPI } from '../services/api';
import Sidebar from '../components/Sidebar';

export default function AdminDashboard() {
  const { user } = useAuthStore();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');
  const [stats, setStats] = useState({
    totalOrders: 0,
    activeOrders: 0,
    revenue: 0,
    users: 0
  });
  const [orders, setOrders] = useState<any[]>([]);
  const [statusBreakdown, setStatusBreakdown] = useState<any>({});

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const { data } = await ordersAPI.getAllOrders();
      const allOrders = data.data || [];
      
      // Calculate stats
      setStats({
        totalOrders: allOrders.length,
        activeOrders: allOrders.filter((o: any) => 
          ['PENDING', 'RECEIVED', 'WASHING', 'DRYING', 'READY'].includes(o.status)
        ).length,
        revenue: allOrders.reduce((sum: number, o: any) => sum + (o.totalPrice || 0), 0),
        users: 5 // Mock data - replace with actual user count
      });

      // Status breakdown
      const breakdown = allOrders.reduce((acc: any, order: any) => {
        acc[order.status] = (acc[order.status] || 0) + 1;
        return acc;
      }, {});
      setStatusBreakdown(breakdown);

      // Recent orders (top 3)
      setOrders(allOrders.slice(0, 3));
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    }
  };

  const getStatusColor = (status: string) => {
    const colors: any = {
      PENDING: 'bg-yellow-100 text-yellow-700',
      RECEIVED: 'bg-blue-100 text-blue-700',
      WASHING: 'bg-purple-100 text-purple-700',
      DRYING: 'bg-orange-100 text-orange-700',
      READY: 'bg-green-100 text-green-700',
      COMPLETED: 'bg-gray-100 text-gray-700'
    };
    return colors[status] || 'bg-gray-100 text-gray-700';
  };

  const getStatusIcon = (status: string) => {
    const icons: any = {
      PENDING: <Clock size={20} className="text-yellow-600" />,
      RECEIVED: <PackageCheck size={20} className="text-blue-600" />,
      WASHING: <Loader size={20} className="text-purple-600" />,
      DRYING: <Wind size={20} className="text-orange-600" />,
      READY: <CheckCircle2 size={20} className="text-green-600" />,
      COMPLETED: <Circle size={20} className="text-gray-600" />
    };
    return icons[status] || <Circle size={20} className="text-gray-600" />;
  };

  const getStatusProgress = (status: string) => {
    const total = stats.totalOrders || 1;
    const count = statusBreakdown[status] || 0;
    return Math.round((count / total) * 100);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <Sidebar userRole="ADMIN" activePage={activeTab} />

      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        <div className="p-8">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Overview</h1>
            <p className="text-gray-600">Welcome back, {user?.fullName?.split(' ')[0]}! Here's what's happening.</p>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            {/* Total Orders */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-blue-100 p-3 rounded-xl">
                  <Package className="text-blue-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Total Orders</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.totalOrders}</div>
              <p className="text-sm text-gray-600">All time</p>
            </div>

            {/* Active Orders */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-yellow-100 p-3 rounded-xl">
                  <Clock className="text-yellow-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Active</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.activeOrders}</div>
              <p className="text-sm text-yellow-600 font-medium">In progress</p>
            </div>

            {/* Revenue */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-green-100 p-3 rounded-xl">
                  <TrendingUp className="text-green-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Revenue</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">${stats.revenue}</div>
              <p className="text-sm text-green-600 font-medium">Total earned</p>
            </div>

            {/* Users */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-purple-100 p-3 rounded-xl">
                  <Users className="text-purple-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Users</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.users}</div>
              <p className="text-sm text-gray-600">Registered</p>
            </div>
          </div>

          {/* Two Column Layout */}
          <div className="grid lg:grid-cols-2 gap-6">
            {/* Order Status Breakdown */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200">
              <h3 className="text-xl font-bold text-gray-900 mb-6">Order Status Breakdown</h3>
              <div className="space-y-4">
                {/* Pending */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      {getStatusIcon('PENDING')}
                      <span className="font-medium text-gray-700">Pending</span>
                    </div>
                    <span className="font-bold text-gray-900">{statusBreakdown.PENDING || 0}</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-600 rounded-full transition-all"
                      style={{ width: `${getStatusProgress('PENDING')}%` }}
                    ></div>
                  </div>
                </div>

                {/* Received */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      {getStatusIcon('RECEIVED')}
                      <span className="font-medium text-gray-700">Received</span>
                    </div>
                    <span className="font-bold text-gray-900">{statusBreakdown.RECEIVED || 0}</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-600 rounded-full transition-all"
                      style={{ width: `${getStatusProgress('RECEIVED')}%` }}
                    ></div>
                  </div>
                </div>

                {/* Washing */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      {getStatusIcon('WASHING')}
                      <span className="font-medium text-gray-700">Washing</span>
                    </div>
                    <span className="font-bold text-gray-900">{statusBreakdown.WASHING || 0}</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-600 rounded-full transition-all"
                      style={{ width: `${getStatusProgress('WASHING')}%` }}
                    ></div>
                  </div>
                </div>

                {/* Drying */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      {getStatusIcon('DRYING')}
                      <span className="font-medium text-gray-700">Drying</span>
                    </div>
                    <span className="font-bold text-gray-900">{statusBreakdown.DRYING || 0}</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-600 rounded-full transition-all"
                      style={{ width: `${getStatusProgress('DRYING')}%` }}
                    ></div>
                  </div>
                </div>

                {/* Ready */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      {getStatusIcon('READY')}
                      <span className="font-medium text-gray-700">Ready</span>
                    </div>
                    <span className="font-bold text-gray-900">{statusBreakdown.READY || 0}</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-600 rounded-full transition-all"
                      style={{ width: `${getStatusProgress('READY')}%` }}
                    ></div>
                  </div>
                </div>

                {/* Completed */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      {getStatusIcon('COMPLETED')}
                      <span className="font-medium text-gray-700">Completed</span>
                    </div>
                    <span className="font-bold text-gray-900">{statusBreakdown.COMPLETED || 0}</span>
                  </div>
                  <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-blue-600 rounded-full transition-all"
                      style={{ width: `${getStatusProgress('COMPLETED')}%` }}
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            {/* Recent Orders */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-gray-900">Recent Orders</h3>
                <button className="text-blue-600 hover:text-blue-700 font-medium text-sm flex items-center gap-1 transition-colors">
                  View all
                  <ChevronRight size={16} />
                </button>
              </div>

              {orders.length === 0 ? (
                <div className="text-center py-12 text-gray-500">
                  <Package size={48} className="mx-auto mb-3 opacity-30" />
                  <p>No orders yet</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {orders.map((order) => (
                    <div key={order.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-all">
                      <div>
                        <p className="font-bold text-gray-900 mb-1">WW-2026-{String(order.id).slice(0, 3)}</p>
                        <p className="text-sm text-gray-600">
                          {order.user?.fullName} • {order.service?.name}
                        </p>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                        <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(order.status)}`}>
                          {order.status.charAt(0) + order.status.slice(1).toLowerCase()}
                        </span>
                        <span className="font-bold text-gray-900">${order.totalPrice}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}