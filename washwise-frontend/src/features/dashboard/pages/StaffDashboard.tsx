import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Package, 
  Clock, 
  CheckCircle, 
  TrendingUp,
  User,
  ArrowRight,
  Filter,
  CheckCircle2,
  XCircle,
  Droplets,
  Wind
} from 'lucide-react';
import { ordersAPI } from '../../../shared/services/api';
import Sidebar from '../../../shared/components/Sidebar';

export default function StaffDashboard() {
  const navigate = useNavigate();
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [stats, setStats] = useState({
    total: 0,
    active: 0,
    completed: 0,
    pending: 0
  });
  const [orders, setOrders] = useState<any[]>([]);
  const [filteredOrders, setFilteredOrders] = useState<any[]>([]);
  const [filterCounts, setFilterCounts] = useState<any>({});

  useEffect(() => {
    fetchOrders();
  }, []);

  useEffect(() => {
    filterOrders();
  }, [activeFilter, orders]);

  const fetchOrders = async () => {
    try {
      const { data } = await ordersAPI.getAllOrders();
      const allOrders = data.data || [];
      
      setOrders(allOrders);
      
      // Calculate stats
      setStats({
        total: allOrders.length,
        active: allOrders.filter((o: any) => 
          ['PENDING', 'RECEIVED', 'WASHING', 'DRYING', 'READY'].includes(o.status)
        ).length,
        completed: allOrders.filter((o: any) => o.status === 'COMPLETED').length,
        pending: allOrders.filter((o: any) => o.status === 'PENDING').length
      });

      // Calculate filter counts
      const counts: any = {
        ALL: allOrders.length,
        PENDING: 0,
        RECEIVED: 0,
        WASHING: 0,
        DRYING: 0,
        READY: 0,
        COMPLETED: 0,
        CANCELLED: 0
      };

      allOrders.forEach((order: any) => {
        counts[order.status] = (counts[order.status] || 0) + 1;
      });

      setFilterCounts(counts);
    } catch (error) {
      console.error('Failed to fetch orders:', error);
    }
  };

  const filterOrders = () => {
    if (activeFilter === 'ALL') {
      setFilteredOrders(orders);
    } else {
      setFilteredOrders(orders.filter(o => o.status === activeFilter));
    }
  };

  const handleStatusUpdate = async (orderId: string, newStatus: string) => {
    try {
      await ordersAPI.updateOrder(orderId, { status: newStatus });
      fetchOrders(); // Refresh data
    } catch (error) {
      console.error('Failed to update order status:', error);
    }
  };

  const getNextStatus = (currentStatus: string) => {
    const flow: any = {
      'PENDING': 'RECEIVED',
      'RECEIVED': 'WASHING',
      'WASHING': 'DRYING',
      'DRYING': 'READY',
      'READY': 'COMPLETED'
    };
    return flow[currentStatus];
  };

  const getStatusColor = (status: string) => {
    const colors: any = {
      PENDING: 'bg-yellow-100 text-yellow-700 border-yellow-200',
      RECEIVED: 'bg-blue-100 text-blue-700 border-blue-200',
      WASHING: 'bg-purple-100 text-purple-700 border-purple-200',
      DRYING: 'bg-orange-100 text-orange-700 border-orange-200',
      READY: 'bg-green-100 text-green-700 border-green-200',
      COMPLETED: 'bg-gray-100 text-gray-700 border-gray-200',
      CANCELLED: 'bg-red-100 text-red-700 border-red-200'
    };
    return colors[status] || 'bg-gray-100 text-gray-700 border-gray-200';
  };

  // Maps statuses to Lucide React icons instead of Emojis
  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING': return <Clock size={14} />;
      case 'RECEIVED': return <Package size={14} />;
      case 'WASHING': return <Droplets size={14} />;
      case 'DRYING': return <Wind size={14} />;
      case 'READY': return <CheckCircle2 size={14} />;
      case 'COMPLETED': return <CheckCircle size={14} />;
      case 'CANCELLED': return <XCircle size={14} />;
      default: return <Package size={14} />;
    }
  };

  const getActionButtonColor = (status: string) => {
    const colors: any = {
      'RECEIVED': 'bg-purple-600 hover:bg-purple-700',
      'WASHING': 'bg-orange-600 hover:bg-orange-700',
      'DRYING': 'bg-green-600 hover:bg-green-700',
      'READY': 'bg-blue-600 hover:bg-blue-700',
      'COMPLETED': 'bg-gray-600 hover:bg-gray-700'
    };
    return colors[getNextStatus(status)] || 'bg-blue-600 hover:bg-blue-700';
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: '2-digit', day: '2-digit' });
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <Sidebar userRole="STAFF" activePage="assigned-orders" />

      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        <div className="p-8">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Staff Dashboard</h1>
            <p className="text-gray-600">Manage and update order statuses</p>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            {/* Total */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-blue-100 p-3 rounded-xl">
                  <Package className="text-blue-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Total</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.total}</div>
              <p className="text-sm text-gray-600">All orders</p>
            </div>

            {/* Active */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-yellow-100 p-3 rounded-xl">
                  <Clock className="text-yellow-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Active</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.active}</div>
              <p className="text-sm text-yellow-600 font-medium">In progress</p>
            </div>

            {/* Done */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-green-100 p-3 rounded-xl">
                  <CheckCircle className="text-green-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Done</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.completed}</div>
              <p className="text-sm text-green-600 font-medium">Completed</p>
            </div>

            {/* Pending */}
            <div className="bg-white p-6 rounded-2xl border border-gray-200 hover:shadow-lg transition-all">
              <div className="flex items-start justify-between mb-4">
                <div className="bg-purple-100 p-3 rounded-xl">
                  <TrendingUp className="text-purple-600" size={24} />
                </div>
                <span className="text-xs font-medium text-gray-500 uppercase">Pending</span>
              </div>
              <div className="text-4xl font-bold text-gray-900 mb-1">{stats.pending}</div>
              <p className="text-sm text-gray-600">Awaiting action</p>
            </div>
          </div>

          {/* Orders Table */}
          <div className="bg-white rounded-2xl border border-gray-200">
            {/* Filters */}
            <div className="p-6 border-b border-gray-200">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Filter size={20} className="text-gray-400" />
                  <span className="font-medium text-gray-700">Filter:</span>
                </div>
                <span className="text-sm text-gray-500">{filteredOrders.length} orders</span>
              </div>
              
              <div className="flex flex-wrap gap-2 mt-4">
                <button
                  onClick={() => setActiveFilter('ALL')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'ALL'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  All Orders
                </button>
                
                <button
                  onClick={() => setActiveFilter('PENDING')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'PENDING'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Pending ({filterCounts.PENDING || 0})
                </button>

                <button
                  onClick={() => setActiveFilter('RECEIVED')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'RECEIVED'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Received ({filterCounts.RECEIVED || 0})
                </button>

                <button
                  onClick={() => setActiveFilter('WASHING')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'WASHING'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Washing ({filterCounts.WASHING || 0})
                </button>

                <button
                  onClick={() => setActiveFilter('DRYING')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'DRYING'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Drying ({filterCounts.DRYING || 0})
                </button>

                <button
                  onClick={() => setActiveFilter('READY')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'READY'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Ready ({filterCounts.READY || 0})
                </button>

                <button
                  onClick={() => setActiveFilter('COMPLETED')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'COMPLETED'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Completed ({filterCounts.COMPLETED || 0})
                </button>

                <button
                  onClick={() => setActiveFilter('CANCELLED')}
                  className={`px-4 py-2 rounded-lg font-medium text-sm transition-all ${
                    activeFilter === 'CANCELLED'
                      ? 'bg-red-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Cancelled ({filterCounts.CANCELLED || 0})
                </button>
              </div>
            </div>

            {/* Table Header */}
            <div className="grid grid-cols-7 gap-4 px-6 py-4 bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-500 uppercase tracking-wider">
              <div>ORDER #</div>
              <div>CUSTOMER</div>
              <div>SERVICE</div>
              <div>WEIGHT</div>
              <div>PICKUP</div>
              <div>STATUS</div>
              <div>ACTION</div>
            </div>

            {/* Table Body */}
            <div className="divide-y divide-gray-200">
              {filteredOrders.length === 0 ? (
                <div className="text-center py-12 text-gray-500">
                  <Package size={48} className="mx-auto mb-3 opacity-30" />
                  <p>No orders found</p>
                </div>
              ) : (
                filteredOrders.map((order) => (
                  <div key={order.id} className="grid grid-cols-7 gap-4 px-6 py-4 items-center hover:bg-gray-50 transition-colors">
                    {/* Order Number */}
                    <div>
                      <p className="font-bold text-blue-600">WW-2026-{String(order.id).slice(0, 3)}</p>
                    </div>

                    {/* Customer */}
                    <div>
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0">
                          <User size={16} className="text-blue-600" />
                        </div>
                        <div className="min-w-0">
                          <p className="font-medium text-gray-900 text-sm truncate">
                            {order.user?.fullName || 'Unknown'}
                          </p>
                          <p className="text-xs text-gray-500 truncate">
                            {order.user?.email || 'N/A'}
                          </p>
                        </div>
                      </div>
                    </div>

                    {/* Service */}
                    <div>
                      <p className="text-sm text-gray-900 font-medium">{order.service?.name || 'N/A'}</p>
                    </div>

                    {/* Weight */}
                    <div>
                      <p className="text-sm text-gray-900 font-medium">{order.weightKg || 0} kg</p>
                    </div>

                    {/* Pickup */}
                    <div>
                      <p className="text-sm text-gray-900">{formatDate(order.createdAt)}</p>
                      <p className="text-xs text-gray-500">{formatTime(order.createdAt)}</p>
                    </div>

                    {/* Status */}
                    <div>
                      <span className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold border ${getStatusColor(order.status)}`}>
                        {getStatusIcon(order.status)}
                        {order.status.charAt(0) + order.status.slice(1).toLowerCase()}
                      </span>
                    </div>

                    {/* Action */}
                    <div>
                      {order.status === 'COMPLETED' || order.status === 'CANCELLED' ? (
                        <span className={`inline-flex items-center gap-1 font-medium text-sm ${order.status === 'COMPLETED' ? 'text-green-600' : 'text-red-600'}`}>
                          {order.status === 'COMPLETED' ? <CheckCircle size={16} /> : <XCircle size={16} />}
                          {order.status === 'COMPLETED' ? 'Done' : 'Cancelled'}
                        </span>
                      ) : (
                        <button
                          onClick={() => handleStatusUpdate(order.id, getNextStatus(order.status))}
                          className={`inline-flex items-center gap-1 px-4 py-2 rounded-lg text-white font-medium text-sm transition-all ${getActionButtonColor(order.status)}`}
                        >
                          <ArrowRight size={16} />
                          {getNextStatus(order.status).charAt(0) + getNextStatus(order.status).slice(1).toLowerCase()}
                        </button>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}