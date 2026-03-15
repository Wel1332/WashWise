import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Shirt, Clock, DollarSign, ChevronLeft, ChevronRight, Loader, AlertCircle } from 'lucide-react';
import { servicesAPI } from '../services/api';

interface Service {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  duration: string;
  imageUrl?: string;
}

export default function Services() {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Wrapped in useCallback to prevent missing dependency warnings
  const fetchServices = useCallback(async () => {
    try {
      setLoading(true);
      const { data } = await servicesAPI.getServices(page, 10);
      setServices(data.data.content);
      setTotalPages(data.data.totalPages);
    } catch (err) { // <-- Removed explicit 'any'
      console.error('Failed to fetch services:', err);
      setError('Failed to load services');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchServices();
  }, [fetchServices]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <Loader size={48} className="animate-spin mx-auto text-blue-600 mb-4" />
          <p className="text-xl text-gray-600">Loading services...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white p-8 rounded-xl border border-red-200 text-center max-w-md">
          <AlertCircle size={48} className="mx-auto text-red-500 mb-4" />
          <p className="text-xl text-red-600 font-semibold">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 py-16">
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4 flex items-center gap-3">
            <div className="bg-blue-100 p-3 rounded-lg">
              <Shirt size={36} className="text-blue-600" />
            </div>
            Our Services
          </h1>
          <p className="text-gray-600 text-lg">Choose from our premium laundry and dry-cleaning services</p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {services.map((service) => (
            <div key={service.id} className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:border-blue-300 hover:shadow-lg transition">
              
              {/* NEW SERVICE IMAGE */}
              {service.imageUrl && (
                <img 
                  src={`http://localhost:8080${service.imageUrl}`}
                  alt={service.name}
                  className="w-full h-48 object-cover bg-gray-200"
                />
              )}
              
              <div className="p-6">
                <h3 className="text-xl font-bold text-gray-900 mb-2">{service.name}</h3>
                <p className="text-gray-600 mb-4 line-clamp-2 text-sm">{service.description}</p>
                
                <div className="mb-6">
                  <span className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-sm font-semibold">
                    {service.category}
                  </span>
                </div>
                
                <div className="flex justify-between items-center mb-6 pb-6 border-b border-gray-200">
                  <div className="flex items-center gap-2">
                    <DollarSign size={20} className="text-green-600" />
                    <span className="text-2xl font-bold text-gray-900">{service.price}</span>
                  </div>
                  <div className="flex items-center gap-2 text-gray-600">
                    <Clock size={18} />
                    <span className="text-sm font-medium">{service.duration}</span>
                  </div>
                </div>
                
                <Link
                  to={`/services/${service.id}`}
                  className="w-full block bg-blue-600 hover:bg-blue-700 text-white text-center py-3 rounded-lg font-semibold transition"
                >
                  View Details
                </Link>
              </div>
            </div>
          ))}
        </div>
        
        {/* PAGINATION */}
        <div className="flex justify-center items-center gap-6">
          <button
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0}
            className="flex items-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed font-medium transition"
          >
            <ChevronLeft size={20} />
            Previous
          </button>
          <span className="text-gray-700 font-semibold min-w-[150px] text-center">
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage(p => p + 1)}
            disabled={page >= totalPages - 1}
            className="flex items-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed font-medium transition"
          >
            Next
            <ChevronRight size={20} />
          </button>
        </div>
      </div>
    </div>
  );
}