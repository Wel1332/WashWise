import { useState } from 'react';
import { Star, AlertCircle, Loader, Send } from 'lucide-react';

interface ReviewFormProps {
  serviceId: string;
  onSubmit: (data: ReviewData) => Promise<void>;
}

export interface ReviewData {
  serviceId: string;
  rating: number;
  comment: string;
}

export default function ReviewForm({ serviceId, onSubmit }: ReviewFormProps) {
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (rating < 1 || rating > 5) {
      setError('Rating must be between 1 and 5');
      return;
    }

    if (comment.trim().length < 10) {
      setError('Comment must be at least 10 characters');
      return;
    }

    setLoading(true);
    try {
      await onSubmit({ serviceId, rating, comment });
      setSuccess(true);
      setRating(5);
      setComment('');
      setTimeout(() => setSuccess(false), 3000);
    } catch (err: any) {
      setError(err.message || 'Failed to submit review');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gradient-to-br from-blue-50 to-purple-50 rounded-xl border-2 border-blue-200 p-8">
      <h3 className="text-2xl font-bold text-gray-900 mb-6 flex items-center gap-2">
        <Star size={28} className="text-yellow-500" />
        Share Your Experience
      </h3>

      {error && (
        <div className="bg-red-50 text-red-700 p-4 rounded-lg mb-6 flex items-center gap-3 border border-red-200">
          <AlertCircle size={20} />
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 text-green-700 p-4 rounded-lg mb-6 flex items-center gap-3 border border-green-200">
          <Star size={20} />
          Thank you! Your review has been submitted.
        </div>
      )}

      <form onSubmit={handleSubmit}>
        {/* STAR RATING */}
        <div className="mb-8">
          <label className="block text-gray-900 mb-4 font-bold text-lg">How would you rate this service?</label>
          <div className="flex gap-3">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                onClick={() => setRating(star)}
                className="transition transform hover:scale-110"
              >
                <Star
                  size={40}
                  className={star <= rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}
                />
              </button>
            ))}
          </div>
          <p className="text-gray-600 text-sm mt-3 font-semibold">Rating: {rating}/5 Stars</p>
        </div>

        {/* COMMENT */}
        <div className="mb-6">
          <label className="block text-gray-900 mb-2 font-bold">Your Review</label>
          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value.slice(0, 200))}
            placeholder="Tell us what you think about our service..."
            rows={4}
            className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent text-gray-900"
          />
          <div className="flex justify-between items-center mt-2">
            <p className="text-gray-500 text-sm">{comment.length}/200 characters</p>
          </div>
        </div>

        {/* SUBMIT BUTTON */}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white py-3 rounded-lg font-bold disabled:opacity-50 flex items-center justify-center gap-2 transition"
        >
          {loading ? (
            <>
              <Loader size={18} className="animate-spin" />
              Submitting...
            </>
          ) : (
            <>
              <Send size={18} />
              Submit Review
            </>
          )}
        </button>
      </form>
    </div>
  );
}