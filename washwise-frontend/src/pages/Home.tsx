import { Link } from 'react-router-dom';
import { Sparkles, Zap, DollarSign, Droplets, Shirt, ArrowRight, CheckCircle } from 'lucide-react';

export default function Home() {
  return (
    <div className="bg-white">
      {/* HERO SECTION */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white">
        <div className="max-w-7xl mx-auto px-4 py-16 sm:py-20">
          <div className="max-w-2xl">
            <h1 className="text-4xl sm:text-5xl font-bold mb-4">Premium Laundry Service</h1>
            <p className="text-xl text-blue-50 mb-8">Professional laundry and dry-cleaning services delivered with excellence and care</p>
            <Link
              to="/services"
              className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 inline-flex items-center gap-2 transition"
            >
              Browse Services <ArrowRight size={20} />
            </Link>
          </div>
        </div>
      </div>

      {/* FEATURES SECTION */}
      <div className="max-w-7xl mx-auto px-4 py-16">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="bg-white p-8 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-lg transition">
            <div className="flex items-center gap-4 mb-4">
              <div className="bg-blue-100 p-3 rounded-lg">
                <Sparkles className="text-blue-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900">Expert Care</h3>
            </div>
            <p className="text-gray-600">Professional handling of all fabric types and delicate garments with precision</p>
          </div>

          <div className="bg-white p-8 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-lg transition">
            <div className="flex items-center gap-4 mb-4">
              <div className="bg-yellow-100 p-3 rounded-lg">
                <Zap className="text-yellow-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900">Fast Turnaround</h3>
            </div>
            <p className="text-gray-600">Quick and efficient service without compromising on quality standards</p>
          </div>

          <div className="bg-white p-8 rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-lg transition">
            <div className="flex items-center gap-4 mb-4">
              <div className="bg-green-100 p-3 rounded-lg">
                <DollarSign className="text-green-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900">Affordable Pricing</h3>
            </div>
            <p className="text-gray-600">Competitive rates for premium laundry care services</p>
          </div>
        </div>
      </div>

      {/* SERVICES SECTION */}
      <div className="bg-gray-50 py-16">
        <div className="max-w-7xl mx-auto px-4">
          <h2 className="text-3xl font-bold text-gray-900 mb-12">Our Services</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Laundry Services */}
            <div className="bg-white p-8 rounded-xl border-l-4 border-blue-600 shadow-sm hover:shadow-md transition">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-blue-100 p-3 rounded-lg">
                  <Droplets className="text-blue-600" size={32} />
                </div>
                <h3 className="text-2xl font-bold text-gray-900">Laundry Services</h3>
              </div>
              <ul className="text-gray-700 space-y-3">
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Regular Wash & Iron
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Delicate Garments
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Bedding & Linens
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Stain Removal
                </li>
              </ul>
            </div>

            {/* Dry Cleaning */}
            <div className="bg-white p-8 rounded-xl border-l-4 border-purple-600 shadow-sm hover:shadow-md transition">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-purple-100 p-3 rounded-lg">
                  <Shirt className="text-purple-600" size={32} />
                </div>
                <h3 className="text-2xl font-bold text-gray-900">Dry Cleaning</h3>
              </div>
              <ul className="text-gray-700 space-y-3">
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Professional Suits
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Formal Wear
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Evening Gowns
                </li>
                <li className="flex items-center gap-3">
                  <CheckCircle size={20} className="text-green-600 flex-shrink-0" /> Leather & Suede
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      {/* CTA SECTION */}
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 py-16">
          <div className="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl p-12 border border-blue-200 text-center">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">Ready to Experience Premium Laundry Care?</h2>
            <p className="text-gray-600 mb-8 text-lg">Book your service today and enjoy professional care for your clothes</p>
            <Link
              to="/services"
              className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 inline-flex items-center gap-2 transition"
            >
              View All Services <ArrowRight size={20} />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}