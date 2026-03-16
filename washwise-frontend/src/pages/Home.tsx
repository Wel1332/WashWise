import { Link } from 'react-router-dom';
import { Droplets, Package, Clock, CheckCircle, TrendingUp, Shield, Award, Users, Zap, Leaf, Menu, X } from 'lucide-react';
import { useState } from 'react';

export default function Home() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-white">
      {/* ========== NAVIGATION BAR ========== */}
      <nav className="bg-white/70 backdrop-blur-md border-b border-white/20 sticky top-0 z-50 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2">
              <div className="bg-blue-600 p-2 rounded-xl shadow-lg">
                <Droplets className="text-white" size={24} />
              </div>
              <span className="text-xl font-bold text-gray-900">WashWise</span>
            </Link>

            {/* Desktop Navigation */}
            <div className="hidden md:flex items-center gap-8">
              <a href="#services" className="text-gray-700 hover:text-gray-900 font-medium transition-colors">
                Services
              </a>
              <a href="#about" className="text-gray-700 hover:text-gray-900 font-medium transition-colors">
                About
              </a>
              <a href="#contact" className="text-gray-700 hover:text-gray-900 font-medium transition-colors">
                Contact
              </a>
              <Link 
                to="/login" 
                className="text-gray-700 hover:text-gray-900 font-medium transition-colors"
              >
                Login
              </Link>
              <Link 
                to="/register" 
                className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-lg font-semibold transition-all shadow-lg hover:shadow-xl"
              >
                Get Started
              </Link>
            </div>

            {/* Mobile Menu Button */}
            <button 
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-lg hover:bg-white/50 transition-colors"
            >
              {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden border-t border-white/20 bg-white/80 backdrop-blur-md">
            <div className="px-4 py-4 space-y-3">
              <a 
                href="#services" 
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-700 hover:text-gray-900 font-medium py-2"
              >
                Services
              </a>
              <a 
                href="#about" 
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-700 hover:text-gray-900 font-medium py-2"
              >
                About
              </a>
              <a 
                href="#contact" 
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-700 hover:text-gray-900 font-medium py-2"
              >
                Contact
              </a>
              <Link 
                to="/login" 
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-700 hover:text-gray-900 font-medium py-2"
              >
                Login
              </Link>
              <Link 
                to="/register" 
                onClick={() => setMobileMenuOpen(false)}
                className="block bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-lg font-semibold text-center shadow-lg"
              >
                Get Started
              </Link>
            </div>
          </div>
        )}
      </nav>
      {/* ========== END NAVIGATION BAR ========== */}

      {/* Hero Section */}
      <section className="bg-gradient-to-br from-blue-600 via-blue-700 to-blue-800 text-white py-20 lg:py-32">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            {/* Left Content */}
            <div>
              <div className="inline-flex items-center gap-2 bg-blue-500/30 px-4 py-2 rounded-full mb-6">
                <Zap size={16} className="text-blue-200" />
                <span className="text-sm font-medium text-blue-100">Fast & Reliable Service</span>
              </div>

              <h1 className="text-5xl lg:text-6xl font-bold mb-6 leading-tight">
                Professional Laundry Service at Your Doorstep
              </h1>

              <p className="text-lg text-blue-100 mb-8 leading-relaxed">
                Book, track, and manage your laundry services with ease. Save time and let us handle your clothes with care.
              </p>

              <div className="flex flex-wrap gap-4 mb-12">
                <Link 
                  to="/register" 
                  className="bg-white text-blue-600 hover:bg-blue-50 px-8 py-4 rounded-xl font-semibold transition-all shadow-lg hover:shadow-xl flex items-center gap-2"
                >
                  <Package size={20} />
                  Book Laundry Now
                </Link>
                <a 
                  href="#services" 
                  className="bg-blue-700 hover:bg-blue-600 text-white px-8 py-4 rounded-xl font-semibold transition-all border-2 border-blue-500"
                >
                  Learn More
                </a>
              </div>

              {/* Stats */}
              <div className="grid grid-cols-3 gap-8">
                <div>
                  <div className="text-3xl font-bold mb-1">10K+</div>
                  <div className="text-blue-200 text-sm">Happy Customers</div>
                </div>
                <div>
                  <div className="text-3xl font-bold mb-1">99%</div>
                  <div className="text-blue-200 text-sm">Satisfaction Rate</div>
                </div>
                <div>
                  <div className="text-3xl font-bold mb-1">24h</div>
                  <div className="text-blue-200 text-sm">Fast Turnaround</div>
                </div>
              </div>
            </div>

            {/* Right Image */}
            <div className="relative">
              <div className="relative rounded-2xl overflow-hidden shadow-2xl">
                <img 
                  src="https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?w=800&h=600&fit=crop" 
                  alt="Laundry Service" 
                  className="w-full h-auto"
                />
                <div className="absolute bottom-6 left-6 bg-white px-6 py-4 rounded-xl shadow-lg flex items-center gap-3">
                  <CheckCircle className="text-green-600" size={24} />
                  <div>
                    <div className="font-bold text-gray-900">10K+</div>
                    <div className="text-sm text-gray-600">Orders Completed</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section id="services" className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Our Services</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Choose from our range of professional laundry services with transparent pricing
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* Wash & Fold */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200 hover:shadow-lg transition-all group">
              <div className="bg-blue-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6 group-hover:bg-blue-600 transition-colors">
                <Droplets className="text-blue-600 group-hover:text-white transition-colors" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">Wash & Fold</h3>
              <p className="text-gray-600 text-sm mb-4">Professional washing and folding service</p>
              <div className="text-3xl font-bold text-blue-600 mb-4">$12<span className="text-lg text-gray-500">/kg</span></div>
              <Link to="/register" className="text-blue-600 font-semibold text-sm hover:text-blue-700 transition-colors inline-flex items-center gap-1">
                Book now →
              </Link>
            </div>

            {/* Dry Clean */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200 hover:shadow-lg transition-all group">
              <div className="bg-purple-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6 group-hover:bg-purple-600 transition-colors">
                <Clock className="text-purple-600 group-hover:text-white transition-colors" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">Dry Clean</h3>
              <p className="text-gray-600 text-sm mb-4">Specialized dry cleaning for delicate items</p>
              <div className="text-3xl font-bold text-purple-600 mb-4">$8<span className="text-lg text-gray-500">/kg</span></div>
              <Link to="/register" className="text-purple-600 font-semibold text-sm hover:text-purple-700 transition-colors inline-flex items-center gap-1">
                Book now →
              </Link>
            </div>

            {/* Iron & Press */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200 hover:shadow-lg transition-all group">
              <div className="bg-green-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6 group-hover:bg-green-600 transition-colors">
                <CheckCircle className="text-green-600 group-hover:text-white transition-colors" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">Iron & Press</h3>
              <p className="text-gray-600 text-sm mb-4">Expert ironing and pressing service</p>
              <div className="text-3xl font-bold text-green-600 mb-4">$10<span className="text-lg text-gray-500">/kg</span></div>
              <Link to="/register" className="text-green-600 font-semibold text-sm hover:text-green-700 transition-colors inline-flex items-center gap-1">
                Book now →
              </Link>
            </div>

            {/* Pickup & Delivery */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200 hover:shadow-lg transition-all group">
              <div className="bg-orange-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6 group-hover:bg-orange-600 transition-colors">
                <TrendingUp className="text-orange-600 group-hover:text-white transition-colors" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">Pickup & Delivery</h3>
              <p className="text-gray-600 text-sm mb-4">Complete service with free delivery</p>
              <div className="text-3xl font-bold text-orange-600 mb-4">$15<span className="text-lg text-gray-500">/kg</span></div>
              <Link to="/register" className="text-orange-600 font-semibold text-sm hover:text-orange-700 transition-colors inline-flex items-center gap-1">
                Book now →
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section id="about" className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">How It Works</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Simple steps to get your laundry done professionally
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {/* Step 1 */}
            <div className="text-center relative">
              <div className="bg-blue-100 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-6">
                <Package className="text-blue-600" size={32} />
              </div>
              <div className="absolute top-8 h-0.5 bg-gray-200 hidden md:block" style={{ left: 'calc(50% + 4rem)', width: 'calc(100% - 6rem)' }}></div>
              <div className="bg-white rounded-full w-8 h-8 flex items-center justify-center mx-auto mb-4 text-sm font-bold text-gray-400 border-2 border-gray-200">
                1
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Book Service</h3>
              <p className="text-gray-600">Select your service type and schedule a convenient pickup time</p>
            </div>

            {/* Step 2 */}
            <div className="text-center relative">
              <div className="bg-blue-100 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-6">
                <Droplets className="text-blue-600" size={32} />
              </div>
              <div className="absolute top-8 h-0.5 bg-gray-200 hidden md:block" style={{ left: 'calc(50% + 4rem)', width: 'calc(100% - 6rem)' }}></div>
              <div className="bg-white rounded-full w-8 h-8 flex items-center justify-center mx-auto mb-4 text-sm font-bold text-gray-400 border-2 border-gray-200">
                2
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">We Process</h3>
              <p className="text-gray-600">Our expert team collects and processes your laundry with care</p>
            </div>

            {/* Step 3 */}
            <div className="text-center">
              <div className="bg-blue-100 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-6">
                <TrendingUp className="text-blue-600" size={32} />
              </div>
              <div className="bg-white rounded-full w-8 h-8 flex items-center justify-center mx-auto mb-4 text-sm font-bold text-gray-400 border-2 border-gray-200">
                3
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Delivery</h3>
              <p className="text-gray-600">Clean, fresh clothes delivered right back to your doorstep</p>
            </div>
          </div>
        </div>
      </section>

      {/* Why Choose WashWise */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Why Choose WashWise</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Premium features for a premium service
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200">
              <div className="bg-blue-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6">
                <Clock className="text-blue-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Real-Time Tracking</h3>
              <p className="text-gray-600">Monitor your order status in real-time from pickup to delivery</p>
            </div>

            {/* Feature 2 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200">
              <div className="bg-yellow-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6">
                <Award className="text-yellow-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Quality Guarantee</h3>
              <p className="text-gray-600">Professional care with 100% satisfaction guarantee</p>
            </div>

            {/* Feature 3 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200">
              <div className="bg-green-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6">
                <Zap className="text-green-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Fast Turnaround</h3>
              <p className="text-gray-600">Quick processing with same-day or next-day delivery options</p>
            </div>

            {/* Feature 4 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200">
              <div className="bg-purple-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6">
                <Shield className="text-purple-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Secure Payment</h3>
              <p className="text-gray-600">Safe and secure payment processing with multiple options</p>
            </div>

            {/* Feature 5 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200">
              <div className="bg-red-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6">
                <Users className="text-red-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Expert Staff</h3>
              <p className="text-gray-600">Trained professionals handling your clothes with expertise</p>
            </div>

            {/* Feature 6 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200">
              <div className="bg-teal-100 w-14 h-14 rounded-xl flex items-center justify-center mb-6">
                <Leaf className="text-teal-600" size={28} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">Eco-Friendly</h3>
              <p className="text-gray-600">Environmentally conscious cleaning methods and products</p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section id="contact" className="bg-gradient-to-br from-blue-600 via-blue-700 to-blue-800 text-white py-20">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-4xl font-bold mb-4">Ready to Experience Premium Laundry Service?</h2>
          <p className="text-xl text-blue-100 mb-8">
            Join thousands of satisfied customers who trust WashWise
          </p>
          <Link 
            to="/register" 
            className="inline-block bg-white text-blue-600 hover:bg-blue-50 px-8 py-4 rounded-xl font-semibold transition-all shadow-lg hover:shadow-xl"
          >
            Get Started Today →
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            {/* Company */}
            <div>
              <div className="flex items-center gap-2 mb-4">
                <div className="bg-blue-600 p-2 rounded-xl">
                  <Droplets className="text-white" size={20} />
                </div>
                <span className="text-lg font-bold">WashWise</span>
              </div>
              <p className="text-gray-400 text-sm">
                Professional laundry platform making your life easier, one load at a time.
              </p>
            </div>

            {/* Services */}
            <div>
              <h4 className="font-bold mb-4 text-sm uppercase tracking-wider text-gray-300">SERVICES</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><a href="#services" className="hover:text-white transition-colors">Wash & Fold</a></li>
                <li><a href="#services" className="hover:text-white transition-colors">Dry Clean</a></li>
                <li><a href="#services" className="hover:text-white transition-colors">Iron & Press</a></li>
                <li><a href="#services" className="hover:text-white transition-colors">Pickup & Delivery</a></li>
              </ul>
            </div>

            {/* Company */}
            <div>
              <h4 className="font-bold mb-4 text-sm uppercase tracking-wider text-gray-300">COMPANY</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><a href="#about" className="hover:text-white transition-colors">About Us</a></li>
                <li><a href="#contact" className="hover:text-white transition-colors">Contact</a></li>
                <li><Link to="/register" className="hover:text-white transition-colors">Careers</Link></li>
                <li><Link to="/register" className="hover:text-white transition-colors">Privacy Policy</Link></li>
              </ul>
            </div>

            {/* Contact */}
            <div>
              <h4 className="font-bold mb-4 text-sm uppercase tracking-wider text-gray-300">CONTACT</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li>support@washwise.com</li>
                <li>+1 (555) 123-4567</li>
                <li>123 Main Street</li>
                <li>New York, NY 10001</li>
              </ul>
            </div>
          </div>

          <div className="border-t border-gray-800 pt-8 text-center text-gray-400 text-sm">
            © 2026 WashWise. All rights reserved. Built with care for your clothes.
          </div>
        </div>
      </footer>
    </div>
  );
}