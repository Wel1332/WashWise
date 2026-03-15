import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  LayoutDashboard, 
  ShoppingCart, 
  Package, 
  UserCircle,
  LogOut,
  Droplets,
  TrendingUp,
  CheckCircle2,
  DollarSign,
  Plus,
  Clock,
  PackageCheck
} from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { ordersAPI } from '../services/api';

export default function CustomerDashboard() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [stats, setStats] = useState({
    totalOrders: 0,
    completed: 0,
    totalSpent: 0
  });
  const [orders, setOrders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCustomerData();
  }, []);

  const fetchCustomerData = async () => {
    try {
      setLoading(true);
      const { data } = await ordersAPI.getMyOrders();
      const myOrders = data.data || [];
      
      setOrders(myOrders);
      
      // Calculate stats
      setStats({
        totalOrders: myOrders.length,
        completed: myOrders.filter((o: any) => o.status === 'COMPLETED').length,
        totalSpent: myOrders.reduce((sum: number, o: any) => sum + (o.totalPrice || 0), 0)
      });
    } catch (error) {
      console.error('Failed to fetch customer data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleBookService = () => {
    navigate('/book-service');
  };

  const getStatusColor = (status: string) => {
    const colors: any = {
      PENDING: 'bg-yellow-100 text-yellow-700 border-yellow-200',
      RECEIVED: 'bg-blue-100 text-blue-700 border-blue-200',
      WASHING: 'bg-purple-100 text-purple-700 border-purple-200',
      DRYING: 'bg-orange-100 text-orange-700 border-orange-200',
      READY: 'bg-green-100 text-green-700 border-green-200',
      COMPLETED: 'bg-gray-100 text-gray-700 border-gray-200'
    };
    return colors[status] || 'bg-gray-100 text-gray-700 border-gray-200';
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
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
              onClick={() => setActiveTab('dashboard')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all ${
                activeTab === 'dashboard'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              <LayoutDashboard size={20} />
              <span>Dashboard</span>
            </button>

            <button
              onClick={() => setActiveTab('book-service')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all ${
                activeTab === 'book-service'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              <ShoppingCart size={20} />
              <span>Book Service</span>
            </button>

            <button
              onClick={() => setActiveTab('my-orders')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all ${
                activeTab === 'my-orders'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              <Package size={20} />
              <span>My Orders</span>
            </button>

            <button
              onClick={() => setActiveTab('profile')}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all ${
                activeTab === 'profile'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
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
              <UserCircle className="text-white" size={24} />
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
        <div className="p-8">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Dashboard</h1>
            <p className="text-gray-600">Welcome back, {user?.fullName?.split(' ')[0]}! 👋</p>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
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

            {/* Completed */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-green-100 p-3 rounded-xl">
                  <TrendingUp className="text-green-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Completed</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.completed}</div>
              <p className="text-sm text-green-600 font-medium">Successfully delivered</p>
            </div>

            {/* Total Spent */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-purple-100 p-3 rounded-xl">
                  <Droplets className="text-purple-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Total Spent</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">${stats.totalSpent.toFixed(2)}</div>
              <p className="text-sm text-gray-600">Lifetime value</p>
            </div>
          </div>

          {/* Two Column Layout */}
          <div className="grid lg:grid-cols-2 gap-6">
            {/* Active Orders / CTA */}
            <div className="bg-white rounded-2xl border border-gray-200 overflow-hidden">
              {orders.filter(o => o.status !== 'COMPLETED').length === 0 ? (
                // No Active Orders - Show CTA
                <div className="bg-gradient-to-br from-blue-600 to-blue-700 p-8 text-white relative overflow-hidden">
                  <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500 rounded-full opacity-20 -mr-32 -mt-32"></div>
                  <div className="absolute bottom-0 left-0 w-48 h-48 bg-blue-400 rounded-full opacity-20 -ml-24 -mb-24"></div>
                  
                  <div className="relative z-10">
                    <div className="bg-white/20 backdrop-blur-sm w-16 h-16 rounded-2xl flex items-center justify-center mb-6">
                      <Plus className="text-white" size={32} />
                    </div>
                    
                    <h3 className="text-3xl font-bold mb-3">Need Laundry Service?</h3>
                    <p className="text-blue-100 mb-6 text-lg">
                      Quick and easy booking in just a few clicks
                    </p>
                    
                    <button
                      onClick={handleBookService}
                      className="bg-white text-blue-600 hover:bg-blue-50 px-6 py-3 rounded-xl font-semibold transition-all flex items-center gap-2 shadow-lg hover:shadow-xl"
                    >
                      <ShoppingCart size={20} />
                      Book New Service
                    </button>
                  </div>
                </div>
              ) : (
                // Active Orders List
                <div className="p-6">
                  <h3 className="text-xl font-bold text-gray-900 mb-6">Active Orders</h3>
                  <div className="space-y-4">
                    {orders
                      .filter(o => o.status !== 'COMPLETED')
                      .slice(0, 3)
                      .map((order) => (
                        <div key={order.id} className="p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-all">
                          <div className="flex items-center justify-between mb-3">
                            <p className="font-bold text-gray-900">WW-2026-{String(order.id).slice(0, 3)}</p>
                            <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${getStatusColor(order.status)}`}>
                              {order.status.charAt(0) + order.status.slice(1).toLowerCase()}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600 mb-2">{order.service?.name}</p>
                          <div className="flex items-center justify-between">
                            <p className="text-xs text-gray-500">{formatDate(order.createdAt)}</p>
                            <p className="font-bold text-gray-900">${order.totalPrice}</p>
                          </div>
                        </div>
                      ))}
                  </div>
                </div>
              )}
            </div>

            {/* Recent Orders */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200">
              <h3 className="text-xl font-bold text-gray-900 mb-6">Recent Orders</h3>
              
              {orders.length === 0 ? (
                <div className="text-center py-16">
                  <div className="bg-gray-100 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Package size={48} className="text-gray-400" />
                  </div>
                  <p className="text-gray-500 font-medium mb-1">No orders yet</p>
                  <p className="text-sm text-gray-400 mb-6">Your order history will appear here</p>
                  <button
                    onClick={handleBookService}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-xl font-semibold transition-all inline-flex items-center gap-2"
                  >
                    <Plus size={20} />
                    Book Your First Service
                  </button>
                </div>
              ) : (
                <div className="space-y-4">
                  {orders.slice(0, 5).map((order) => (
                    <div key={order.id} className="flex items-center gap-4 p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-all cursor-pointer">
                      <div className={`w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0 ${
                        order.status === 'COMPLETED' ? 'bg-green-100' : 'bg-blue-100'
                      }`}>
                        {order.status === 'COMPLETED' ? (
                          <CheckCircle2 className="text-green-600" size={24} />
                        ) : (
                          <Clock className="text-blue-600" size={24} />
                        )}
                      </div>
                      
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between mb-1">
                          <p className="font-bold text-gray-900 text-sm">WW-2026-{String(order.id).slice(0, 3)}</p>
                          <p className="font-bold text-gray-900">${order.totalPrice}</p>
                        </div>
                        <p className="text-sm text-gray-600 mb-1">{order.service?.name}</p>
                        <div className="flex items-center justify-between">
                          <p className="text-xs text-gray-500">{formatDate(order.createdAt)}</p>
                          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${getStatusColor(order.status)}`}>
                            {order.status.charAt(0) + order.status.slice(1).toLowerCase()}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Quick Actions */}
          {orders.length > 0 && (
            <div className="mt-8 grid md:grid-cols-3 gap-4">
              <button
                onClick={handleBookService}
                className="bg-blue-600 hover:bg-blue-700 text-white p-6 rounded-2xl font-semibold transition-all flex items-center justify-center gap-3 shadow-sm hover:shadow-lg"
              >
                <Plus size={24} />
                Book New Service
              </button>
              
              <button
                onClick={() => setActiveTab('my-orders')}
                className="bg-white hover:bg-gray-50 text-gray-900 p-6 rounded-2xl font-semibold transition-all flex items-center justify-center gap-3 border-2 border-gray-200"
              >
                <Package size={24} />
                View All Orders
              </button>
              
              <button
                onClick={() => setActiveTab('profile')}
                className="bg-white hover:bg-gray-50 text-gray-900 p-6 rounded-2xl font-semibold transition-all flex items-center justify-center gap-3 border-2 border-gray-200"
              >
                <UserCircle size={24} />
                My Profile
              </button>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}