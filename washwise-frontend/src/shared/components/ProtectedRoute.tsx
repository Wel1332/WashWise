import { Navigate, useLocation } from 'react-router-dom';
import type { ReactNode } from 'react';
import { useAuthStore } from '../../features/auth/store/authStore';

interface ProtectedRouteProps {
  children: ReactNode;
  roles?: Array<'ADMIN' | 'STAFF' | 'CUSTOMER'>;
}

const dashboardForRole = (role?: string) => {
  switch (role) {
    case 'ADMIN':
      return '/dashboard/admin';
    case 'STAFF':
      return '/dashboard/staff';
    default:
      return '/dashboard';
  }
};

export default function ProtectedRoute({ children, roles }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAuthStore();
  const location = useLocation();

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (roles && roles.length > 0 && !roles.includes(user.role as 'ADMIN' | 'STAFF' | 'CUSTOMER')) {
    return <Navigate to={dashboardForRole(user.role)} replace />;
  }

  return <>{children}</>;
}
