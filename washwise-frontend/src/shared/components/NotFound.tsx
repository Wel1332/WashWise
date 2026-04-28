import { Link } from 'react-router-dom';
import { Home, Droplets } from 'lucide-react';

export default function NotFound() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="max-w-md w-full text-center">
        <div className="inline-flex items-center justify-center bg-blue-600 p-4 rounded-2xl mb-6">
          <Droplets className="text-white" size={36} strokeWidth={2.5} />
        </div>
        <h1 className="text-6xl font-bold text-gray-900 mb-3">404</h1>
        <p className="text-lg text-gray-600 mb-8">
          We couldn&rsquo;t find the page you were looking for.
        </p>
        <Link
          to="/"
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-xl font-semibold transition-colors shadow-sm hover:shadow-md"
        >
          <Home size={18} />
          Back to Home
        </Link>
      </div>
    </div>
  );
}
