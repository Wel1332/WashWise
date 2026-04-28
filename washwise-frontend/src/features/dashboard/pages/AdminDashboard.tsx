import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
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
  Circle,
  Settings,
  Plus,
  X,
  Tag,
  AlignLeft,
  DollarSign,
  Hourglass
} from 'lucide-react';
import { useAuthStore } from '../../../features/auth/store/authStore';
import { ordersAPI, usersAPI, servicesAPI } from '../../../shared/services/api';
import Sidebar from '../../../shared/components/Sidebar';

export default function AdminDashboard() {
  const { user } = useAuthStore();
  const location = useLocation();
  
  const [activeTab, setActiveTab] = useState(location.state?.activeTab || 'overview');
  
  const [stats, setStats] = useState({
    totalOrders: 0,
    activeOrders: 0,
    revenue: 0,
    users: 0
  });
  
  const [orders, setOrders] = useState<any[]>([]);
  const [usersList, setUsersList] = useState<any[]>([]);
  const [servicesList, setServicesList] = useState<any[]>([]);
  const [statusBreakdown, setStatusBreakdown] = useState<any>({});

  // Add Service Modal States
  const [isServiceModalOpen, setIsServiceModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [newService, setNewService] = useState({
    name: '',
    description: '',
    price: '',
    category: 'Wash',
    duration: '24-48 hours',
    isActive: true
  });

  useEffect(() => {
    if (location.state?.activeTab) {
      setActiveTab(location.state.activeTab);
    }
  }, [location.state]);

  useEffect(() => {
    if (activeTab === 'overview' || activeTab === 'orders' || activeTab === 'users') {
      fetchDashboardData();
    }
    if (activeTab === 'services') {
      fetchServicesData();
    }
  }, [activeTab]);

  const fetchDashboardData = async () => {
    try {
      const [ordersRes, usersRes] = await Promise.all([
        ordersAPI.getAllOrders(),
        usersAPI.getAllUsers()
      ]);
      
      const allOrders = ordersRes.data.data || [];
      const allUsers = usersRes.data.data || [];
      
      setOrders(allOrders);
      setUsersList(allUsers);
      
      setStats({
        totalOrders: allOrders.length,
        activeOrders: allOrders.filter((o: any) => 
          ['PENDING', 'RECEIVED', 'WASHING', 'DRYING', 'READY'].includes(o.status)
        ).length,
        revenue: allOrders.reduce((sum: number, o: any) => sum + (parseFloat(o.totalPrice) || 0), 0),
        users: allUsers.length
      });

      const breakdown = allOrders.reduce((acc: any, order: any) => {
        acc[order.status] = (acc[order.status] || 0) + 1;
        return acc;
      }, {});
      setStatusBreakdown(breakdown);

    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    }
  };

  const fetchServicesData = async () => {
    try {
      const { data } = await servicesAPI.getAllServices();
      setServicesList(data.data || []);
    } catch (error) {
      console.error('Failed to fetch services:', error);
    }
  };

  const handleRoleChange = async (userId: string, newRole: string) => {
    try {
      await usersAPI.updateUserRole(userId, newRole);
      setUsersList(usersList.map(u => u.id === userId ? { ...u, role: newRole } : u));
    } catch (error) {
      console.error('Failed to update user role:', error);
      alert('Failed to update user role');
    }
  };

  const handleCreateService = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await servicesAPI.createService({
        ...newService,
        price: parseFloat(newService.price)
      });
      setIsServiceModalOpen(false);
      setNewService({ name: '', description: '', price: '', category: 'Wash', duration: '24-48 hours', isActive: true });
      fetchServicesData(); // Refresh the table
    } catch (error: any) {
      console.error('Failed to create service:', error);
      alert(error.response?.data?.message || 'Failed to create service. Please check your inputs.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const getStatusColor = (status: string) => {
    const colors: any = {
      PENDING: 'bg-yellow-100 text-yellow-700',
      RECEIVED: 'bg-blue-100 text-blue-700',
      WASHING: 'bg-purple-100 text-purple-700',
      DRYING: 'bg-orange-100 text-orange-700',
      READY: 'bg-green-100 text-green-700',
      COMPLETED: 'bg-gray-100 text-gray-700',
      CANCELLED: 'bg-red-100 text-red-700'
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
      <Sidebar userRole="ADMIN" activePage={activeTab} />

      <main className="flex-1 overflow-auto relative">
        
        {/* ==================== OVERVIEW TAB ====================== */}
        {activeTab === 'overview' && (
          <div className="p-8 animate-in fade-in duration-200">
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-gray-900 mb-2">Overview</h1>
              <p className="text-gray-600">Welcome back, {user?.fullName?.split(' ')[0]}! Here's what's happening.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
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

              <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
                <div className="flex items-start justify-between mb-4">
                  <div className="bg-green-100 p-3 rounded-xl">
                    <TrendingUp className="text-green-600" size={24} />
                  </div>
                  <span className="text-xs font-medium text-gray-500 uppercase">Revenue</span>
                </div>
                <div className="text-4xl font-bold text-gray-900 mb-1">₱{stats.revenue.toFixed(0)}</div>
                <p className="text-sm text-green-600 font-medium">Total earned</p>
              </div>

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

            <div className="grid lg:grid-cols-2 gap-6">
              <div className="bg-white p-6 rounded-2xl border border-gray-200">
                <h3 className="text-xl font-bold text-gray-900 mb-6">Order Status Breakdown</h3>
                <div className="space-y-4">
                  {['PENDING', 'RECEIVED', 'WASHING', 'DRYING', 'READY', 'COMPLETED'].map((status) => (
                    <div key={status}>
                      <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center gap-2">
                          {getStatusIcon(status)}
                          <span className="font-medium text-gray-700">
                            {status.charAt(0) + status.slice(1).toLowerCase()}
                          </span>
                        </div>
                        <span className="font-bold text-gray-900">{statusBreakdown[status] || 0}</span>
                      </div>
                      <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                        <div 
                          className="h-full bg-blue-600 rounded-full transition-all duration-500"
                          style={{ width: `${getStatusProgress(status)}%` }}
                        ></div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="bg-white p-6 rounded-2xl border border-gray-200">
                <div className="flex items-center justify-between mb-6">
                  <h3 className="text-xl font-bold text-gray-900">Recent Orders</h3>
                  <button 
                    onClick={() => setActiveTab('orders')}
                    className="text-blue-600 hover:text-blue-700 font-medium text-sm flex items-center gap-1 transition-colors"
                  >
                    View all <ChevronRight size={16} />
                  </button>
                </div>

                {orders.length === 0 ? (
                  <div className="text-center py-12 text-gray-500">
                    <Package size={48} className="mx-auto mb-3 opacity-30" />
                    <p>No orders yet</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {orders.slice(0, 5).map((order) => (
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
                          <span className="font-bold text-gray-900">₱{parseFloat(order.totalPrice).toFixed(0)}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ==================== SERVICES TAB ====================== */}
        {activeTab === 'services' && (
          <div className="p-8 animate-in fade-in duration-200">
            <div className="mb-8 flex justify-between items-end">
              <div>
                <h1 className="text-4xl font-bold text-gray-900 mb-2">Services Management</h1>
                <p className="text-gray-600">Add, update, or remove laundry services.</p>
              </div>
              <button 
                onClick={() => setIsServiceModalOpen(true)}
                className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-xl font-semibold transition-colors shadow-sm flex items-center gap-2"
              >
                <Plus size={18} /> Add New Service
              </button>
            </div>
            
            <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
              <div className="grid grid-cols-5 gap-4 px-6 py-4 bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                <div className="col-span-2">SERVICE NAME</div>
                <div>CATEGORY</div>
                <div>PRICE</div>
                <div>DURATION</div>
              </div>
              <div className="divide-y divide-gray-200">
                {servicesList.map((service) => (
                  <div key={service.id} className="grid grid-cols-5 gap-4 px-6 py-4 items-center hover:bg-gray-50 transition-colors">
                    <div className="col-span-2">
                      <p className="font-bold text-gray-900 text-sm">{service.name}</p>
                      <p className="text-xs text-gray-500 truncate mt-0.5 pr-4">{service.description}</p>
                    </div>
                    <div>
                      <span className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-xs font-medium">
                        {service.category}
                      </span>
                    </div>
                    <div className="text-sm font-bold text-gray-900">₱{parseFloat(service.price).toFixed(0)}/kg</div>
                    <div className="text-sm text-gray-500">{service.duration}</div>
                  </div>
                ))}
                {servicesList.length === 0 && (
                  <div className="p-12 text-center text-gray-500 flex flex-col items-center">
                    <Settings size={48} className="text-gray-300 mb-4" />
                    <p>No services found. Click "Add New Service" to create one.</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ===================== ORDERS TAB ======================= */}
        {activeTab === 'orders' && (
          <div className="p-8 animate-in fade-in duration-200">
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-gray-900 mb-2">All Orders</h1>
              <p className="text-gray-600">View and track all customer orders in the system.</p>
            </div>
            <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
              <div className="grid grid-cols-6 gap-4 px-6 py-4 bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                <div>ORDER ID</div>
                <div>CUSTOMER</div>
                <div>SERVICE</div>
                <div>PRICE</div>
                <div>STATUS</div>
                <div>DATE</div>
              </div>
              <div className="divide-y divide-gray-200">
                {orders.map((order) => (
                  <div key={order.id} className="grid grid-cols-6 gap-4 px-6 py-4 items-center hover:bg-gray-50">
                    <div className="font-bold text-blue-600 text-sm">WW-2026-{String(order.id).slice(0, 3)}</div>
                    <div>
                      <p className="text-sm font-medium text-gray-900">{order.user?.fullName}</p>
                      <p className="text-xs text-gray-500">{order.user?.email}</p>
                    </div>
                    <div className="text-sm text-gray-900">{order.service?.name}</div>
                    <div className="text-sm font-bold text-gray-900">₱{parseFloat(order.totalPrice).toFixed(0)}</div>
                    <div>
                       <span className={`px-2 py-1 rounded-full text-xs font-semibold ${getStatusColor(order.status)}`}>
                         {order.status}
                       </span>
                    </div>
                    <div className="text-sm text-gray-500">
                      {new Date(order.createdAt).toLocaleDateString('en-US')}
                    </div>
                  </div>
                ))}
                {orders.length === 0 && (
                   <div className="p-8 text-center text-gray-500">No orders found.</div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ====================== USERS TAB ======================= */}
        {activeTab === 'users' && (
          <div className="p-8 animate-in fade-in duration-200">
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-gray-900 mb-2">User Management</h1>
              <p className="text-gray-600">Manage customers, staff, and admin accounts.</p>
            </div>
            <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
              <div className="grid grid-cols-5 gap-4 px-6 py-4 bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                <div>NAME</div>
                <div>EMAIL</div>
                <div>ROLE</div>
                <div>JOINED</div>
                <div>ACTION</div>
              </div>
              <div className="divide-y divide-gray-200">
                {usersList.map((u) => (
                  <div key={u.id} className="grid grid-cols-5 gap-4 px-6 py-4 items-center hover:bg-gray-50">
                    <div className="font-bold text-gray-900 text-sm">{u.fullName}</div>
                    <div className="text-sm text-gray-600">{u.email}</div>
                    <div>
                      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        u.role === 'ADMIN' ? 'bg-gray-900 text-white' :
                        u.role === 'STAFF' ? 'bg-purple-100 text-purple-700' :
                        'bg-blue-100 text-blue-700'
                      }`}>
                        {u.role.charAt(0) + u.role.slice(1).toLowerCase()}
                      </span>
                    </div>
                    <div className="text-sm text-gray-500">
                      {new Date(u.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })}
                    </div>
                    <div>
                      {u.id !== user?.id ? (
                        <select
                          value={u.role}
                          onChange={(e) => handleRoleChange(u.id, e.target.value)}
                          className="border border-gray-300 rounded-lg text-sm px-3 py-1.5 focus:ring-2 focus:ring-blue-600 outline-none text-gray-700 bg-white cursor-pointer"
                        >
                          <option value="CUSTOMER">Customer</option>
                          <option value="STAFF">Staff</option>
                          <option value="ADMIN">Admin</option>
                        </select>
                      ) : (
                        <span className="text-xs text-gray-400 italic">Current User</span>
                      )}
                    </div>
                  </div>
                ))}
                {usersList.length === 0 && (
                  <div className="p-8 text-center text-gray-500">No users found.</div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ===================== ADD SERVICE MODAL ===================== */}
        {isServiceModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-3xl shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in duration-200">
              <div className="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50/50">
                <h2 className="text-xl font-bold text-gray-900">Create New Service</h2>
                <button 
                  onClick={() => setIsServiceModalOpen(false)}
                  className="p-2 hover:bg-gray-200 rounded-full transition-colors"
                >
                  <X size={20} className="text-gray-500" />
                </button>
              </div>
              
              <form onSubmit={handleCreateService} className="p-6 space-y-5">
                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <Tag size={16} className="text-blue-600" /> Service Name
                  </label>
                  <input
                    type="text"
                    required
                    value={newService.name}
                    onChange={e => setNewService({...newService, name: e.target.value})}
                    className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent transition-all"
                    placeholder="e.g. Wash & Fold Plus"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                      <DollarSign size={16} className="text-blue-600" /> Price (₱/kg)
                    </label>
                    <input
                      type="number"
                      required
                      min="1"
                      step="0.01"
                      value={newService.price}
                      onChange={e => setNewService({...newService, price: e.target.value})}
                      className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent transition-all"
                      placeholder="50"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                      <Hourglass size={16} className="text-blue-600" /> Duration
                    </label>
                    <input
                      type="text"
                      required
                      value={newService.duration}
                      onChange={e => setNewService({...newService, duration: e.target.value})}
                      className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent transition-all"
                      placeholder="e.g. 2-3 days"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-2">Category</label>
                  <select
                    value={newService.category}
                    onChange={e => setNewService({...newService, category: e.target.value})}
                    className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent transition-all bg-white"
                  >
                    <option value="Wash">Wash</option>
                    <option value="Dry Clean">Dry Clean</option>
                    <option value="Premium">Premium</option>
                    <option value="Ironing">Ironing</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <AlignLeft size={16} className="text-blue-600" /> Description
                  </label>
                  <textarea
                    required
                    value={newService.description}
                    onChange={e => setNewService({...newService, description: e.target.value})}
                    className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent transition-all resize-none h-24"
                    placeholder="Describe what is included in this service..."
                  />
                </div>

                <div className="pt-4 flex gap-3">
                  <button
                    type="button"
                    onClick={() => setIsServiceModalOpen(false)}
                    className="flex-1 px-5 py-3 bg-gray-100 hover:bg-gray-200 text-gray-800 rounded-xl font-semibold transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="flex-1 px-5 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold transition-colors shadow-sm disabled:opacity-50 flex items-center justify-center gap-2"
                  >
                    {isSubmitting ? <Loader size={18} className="animate-spin" /> : 'Save Service'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

      </main>
    </div>
  );
}