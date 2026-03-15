import { Clock, CheckCircle, Truck, Home } from 'lucide-react';

interface TrackingStep {
  status: string;
  label: string;
  description: string;
  timestamp?: string;
  icon: any;
}

interface OrderTrackingProps {
  currentStatus: string;
  scheduledDate: string;
  location: string;
}

export default function OrderTracking({
  currentStatus,
  scheduledDate,
  location,
}: OrderTrackingProps) {
  const steps: TrackingStep[] = [
    {
      status: 'PENDING',
      label: 'Order Placed',
      description: 'Your order has been confirmed',
      icon: Clock,
    },
    {
      status: 'CONFIRMED',
      label: 'Pickup Scheduled',
      description: 'We will pick up your clothes',
      icon: Truck,
    },
    {
      status: 'COMPLETED',
      label: 'Delivered',
      description: 'Your clothes are ready',
      icon: Home,
    },
  ];

  const getStepIndex = (status: string) => {
    return steps.findIndex(s => s.status === status);
  };

  const currentStepIndex = getStepIndex(currentStatus);

  return (
    <div className="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl border-2 border-blue-200 p-8">
      <h3 className="text-2xl font-bold text-gray-900 mb-8">Order Tracking</h3>

      {/* TIMELINE */}
      <div className="space-y-8">
        {steps.map((step, index) => {
          const isCompleted = index < currentStepIndex;
          const isActive = index === currentStepIndex;
          const Icon = step.icon;

          return (
            <div key={step.status}>
              <div className="flex items-start gap-4">
                {/* ICON CIRCLE */}
                <div className={`flex-shrink-0 w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg ${
                  isCompleted ? 'bg-green-500 text-white' :
                  isActive ? 'bg-blue-500 text-white' :
                  'bg-gray-300 text-white'
                }`}>
                  <Icon size={24} />
                </div>

                {/* CONTENT */}
                <div className="flex-grow">
                  <h4 className={`text-lg font-bold ${
                    isCompleted || isActive ? 'text-gray-900' : 'text-gray-500'
                  }`}>
                    {step.label}
                  </h4>
                  <p className="text-gray-600 text-sm mt-1">{step.description}</p>
                  {isActive && (
                    <p className="text-blue-600 text-sm font-semibold mt-2">
                      Scheduled: {new Date(scheduledDate).toLocaleString()}
                    </p>
                  )}
                </div>

                {/* CHECKMARK */}
                {isCompleted && (
                  <CheckCircle size={24} className="text-green-500 flex-shrink-0 mt-1" />
                )}
              </div>

              {/* CONNECTING LINE */}
              {index < steps.length - 1 && (
                <div className="ml-6 h-8 border-l-2 border-gray-300 my-2"></div>
              )}
            </div>
          );
        })}
      </div>

      {/* LOCATION INFO */}
      <div className="mt-8 p-4 bg-white rounded-lg border border-gray-200">
        <p className="text-sm text-gray-600 font-semibold">📍 Pickup Location</p>
        <p className="text-gray-900 font-medium mt-1">{location}</p>
      </div>
    </div>
  );
}