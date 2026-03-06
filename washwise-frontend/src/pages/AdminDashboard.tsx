import { useState, useEffect } from 'react';
import { BarChart3, Shirt, Plus, Settings, AlertCircle, Loader, CheckCircle, Clock } from 'lucide-react';
import { ordersAPI, servicesAPI } from '../services/api';

interface Service {
  id: string;
  name: string;
  price: number;
  category: string;
  isActive: boolean;
}

interface Order {
  id: string;
  serviceName: string;
  email: string;
  status: string;
  totalPrice: number;
  location: string;
  scheduledDate: string;
}

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState<'orders' | 'services'>('orders');
  const [orders, setOrders] = useState<Order[]>([]);
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [showServiceForm, setShowServiceForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    duration: '',
  });

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  const fetchData = async () => {
    try {
      setLoading(true);
      if (activeTab === 'orders') {
        const { data } = await ordersAPI.getAllOrders();
        setOrders(data.data);
      } else {
        const { data } = await servicesAPI.getServices(0, 100);
        setServices(data.data.content);
      }
    } catch (err) {
      console.error('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateService = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await servicesAPI.createService({
        ...formData,
        price: parseFloat(formData.price),
        isActive: true,
      });
      alert('Service created successfully');
      setFormData({ name: '', description: '', price: '', category: '', duration: '' });
      setShowServiceForm(false);
      fetchData();
    } catch (err) {
      alert('Failed to create service');
    }
  };

  const handleUpdateOrderStatus = async (orderId: string, newStatus: string) => {
    try {
      await ordersAPI.updateOrder(orderId, { status: newStatus });
      alert('Order updated successfully');
      fetchData();
    } catch (err) {
      alert('Failed to update order');
    }
  };

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 py-16">
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4 flex items-center gap-3">
            <div className="bg-blue-100 p-3 rounded-lg">
              <BarChart3 size={36} className="text-blue-600" />
            </div>
            Admin Dashboard
          </h1>
          <p className="text-gray-600 text-lg">Manage orders and services</p>
        </div>

        {/* TABS */}
        <div className="flex gap-4 mb-8 border-b border-gray-200">
          <button
            onClick={() => setActiveTab('orders')}
            className={`px-6 py-3 font-semibold flex items-center gap-2 border-b-2 transition ${
              activeTab === 'orders'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-900'
            }`}
          >
            <Clock size={20} />
            Orders
          </button>
          <button
            onClick={() => setActiveTab('services')}
            className={`px-6 py-3 font-semibold flex items-center gap-2 border-b-2 transition ${
              activeTab === 'services'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-900'
            }`}
          >
            <Shirt size={20} />
            Services
          </button>
        </div>

        {/* ORDERS TAB */}
        {activeTab === 'orders' && (
          <div>
            {loading ? (
              <div className="text-center py-12">
                <Loader size={48} className="animate-spin mx-auto text-blue-600 mb-4" />
                <p className="text-gray-600">Loading orders...</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 gap-4">
                {orders.map((order) => (
                  <div key={order.id} className="bg-gray-50 p-6 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-md transition">
                    <div className="flex justify-between items-start mb-4">
                      <div>
                        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2 mb-1">
                          <Shirt size={22} className="text-blue-600" />
                          {order.serviceName}
                        </h3>
                        <p className="text-gray-600 text-sm">{order.email}</p>
                        <p className="text-gray-500 text-sm">{order.location}</p>
                      </div>
                      <span className="text-2xl font-bold text-green-600">${order.totalPrice}</span>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <p className="text-gray-600 text-sm font-semibold mb-1">Scheduled Pickup</p>
                        <p className="text-sm text-gray-900">{new Date(order.scheduledDate).toLocaleString()}</p>
                      </div>
                      <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-2">Update Status</label>
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
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* SERVICES TAB */}
        {activeTab === 'services' && (
          <div>
            <button
              onClick={() => setShowServiceForm(!showServiceForm)}
              className="mb-8 bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-semibold transition flex items-center gap-2"
            >
              <Plus size={20} />
              {showServiceForm ? 'Cancel' : 'Create Service'}
            </button>

            {showServiceForm && (
              <div className="bg-gray-50 p-8 rounded-xl border border-gray-200 mb-8">
                <h3 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
                  <div className="bg-blue-100 p-2 rounded-lg">
                    <Settings size={24} className="text-blue-600" />
                  </div>
                  Create New Service
                </h3>
                <form onSubmit={handleCreateService}>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                    <div>
                      <label className="block text-gray-900 mb-2 font-semibold">Service Name</label>
                      <input
                        type="text"
                        placeholder="e.g., Regular Wash & Iron"
                        value={formData.name}
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-gray-900 mb-2 font-semibold">Price ($)</label>
                      <input
                        type="number"
                        placeholder="0.00"
                        value={formData.price}
                        onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-gray-900 mb-2 font-semibold">Category</label>
                      <input
                        type="text"
                        placeholder="e.g., Laundry, Dry Cleaning"
                        value={formData.category}
                        onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-gray-900 mb-2 font-semibold">Turnaround Time</label>
                      <input
                        type="text"
                        placeholder="e.g., 2-3 days"
                        value={formData.duration}
                        onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                        required
                      />
                    </div>
                  </div>
                  <div className="mb-4">
                    <label className="block text-gray-900 mb-2 font-semibold">Description</label>
                    <textarea
                      placeholder="Service description..."
                      value={formData.description}
                      onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 text-gray-900"
                      rows={4}
                      required
                    />
                  </div>
                  <button
                    type="submit"
                    className="w-full bg-green-600 hover:bg-green-700 text-white py-3 rounded-lg font-semibold transition flex items-center justify-center gap-2"
                  >
                    <Plus size={20} />
                    Create Service
                  </button>
                </form>
              </div>
            )}

            {loading ? (
              <div className="text-center py-12">
                <Loader size={48} className="animate-spin mx-auto text-blue-600 mb-4" />
                <p className="text-gray-600">Loading services...</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {services.map((service) => (
                  <div key={service.id} className="bg-gray-50 p-6 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-md transition">
                    <h3 className="text-lg font-bold text-gray-900 mb-2 flex items-center gap-2">
                      <Shirt size={20} className="text-blue-600" />
                      {service.name}
                    </h3>
                    <p className="text-gray-600 mb-4 text-sm">{service.category}</p>
                    <p className="text-2xl font-bold text-green-600 mb-4">${service.price}</p>
                    <span className={`px-3 py-1 rounded-full text-sm font-semibold flex items-center gap-2 inline-flex ${
                      service.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    }`}>
                      {service.isActive ? (
                        <>
                          <CheckCircle size={16} />
                          Active
                        </>
                      ) : (
                        <>
                          <AlertCircle size={16} />
                          Inactive
                        </>
                      )}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}