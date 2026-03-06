import { Star, User, Calendar } from 'lucide-react';

interface Review {
  id: string;
  userName: string;
  rating: number;
  comment: string;
  createdAt: string;
}

interface ReviewListProps {
  reviews: Review[];
  averageRating: number;
  reviewCount: number;
}

export default function ReviewList({ reviews, averageRating, reviewCount }: ReviewListProps) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-8">
      <div className="mb-8">
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            {[1, 2, 3, 4, 5].map((star) => (
              <Star
                key={star}
                size={24}
                className={star <= Math.round(averageRating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}
              />
            ))}
          </div>
          <div>
            <p className="text-3xl font-bold text-gray-900">{averageRating.toFixed(1)}</p>
            <p className="text-gray-600">{reviewCount} reviews</p>
          </div>
        </div>
      </div>

      <div className="space-y-6">
        {reviews.length === 0 ? (
          <p className="text-gray-600 text-center py-8">No reviews yet. Be the first to review!</p>
        ) : (
          reviews.map((review) => (
            <div key={review.id} className="border-b border-gray-200 pb-6 last:border-b-0">
              <div className="flex justify-between items-start mb-2">
                <div className="flex items-center gap-2">
                  <div className="bg-blue-100 p-2 rounded-full">
                    <User size={18} className="text-blue-600" />
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">{review.userName}</p>
                    <div className="flex items-center gap-2 text-sm text-gray-500">
                      <Calendar size={14} />
                      {new Date(review.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                </div>
                <div className="flex gap-1">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <Star
                      key={star}
                      size={16}
                      className={star <= review.rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}
                    />
                  ))}
                </div>
              </div>
              <p className="text-gray-700">{review.comment}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}