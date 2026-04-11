import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../features/auth/store/authStore';
import { 
  LayoutDashboard, 
  ShoppingCart, 
  Package, 
  UserCircle, 
  LogOut, 
  Droplets,
  User as UserIcon,
  Settings,
  Users,
  type LucideIcon // Added this import
} from 'lucide-react';

// Added interfaces to fix the TypeScript errors
interface MenuItem {
  id: string;
  label: string;
  icon: LucideIcon;
  path: string;
  state?: string; // The '?' makes it optional, fixing the error
}

interface RoleConfigDetails {
  portalName: string;
  badgeColor: string;
  badgeText: string;
  menuItems: MenuItem[];
}

interface SidebarProps {
  userRole: 'CUSTOMER' | 'STAFF' | 'ADMIN';
  activePage?: string;
}

export default function Sidebar({ userRole, activePage }: SidebarProps) {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Applied the type to roleConfig
  const roleConfig: Record<string, RoleConfigDetails> = {
    CUSTOMER: {
      portalName: 'Customer Portal',
      badgeColor: 'bg-blue-100 text-blue-700',
      badgeText: 'Customer',
      menuItems: [
        { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, path: '/dashboard' },
        { id: 'book-service', label: 'Book Service', icon: ShoppingCart, path: '/book-service' },
        { id: 'my-orders', label: 'My Orders', icon: Package, path: '/my-orders' },
        { id: 'profile', label: 'Profile', icon: UserCircle, path: '/profile' }
      ]
    },
    STAFF: {
      portalName: 'Staff Portal',
      badgeColor: 'bg-blue-100 text-blue-700',
      badgeText: 'Staff',
      menuItems: [
        { id: 'assigned-orders', label: 'Assigned Orders', icon: Package, path: '/dashboard/staff' }
      ]
    },
    ADMIN: {
      portalName: 'Admin Panel',
      badgeColor: 'bg-gray-900 text-white',
      badgeText: 'Admin',
      menuItems: [
        { id: 'overview', label: 'Overview', icon: LayoutDashboard, path: '/dashboard/admin', state: 'overview' },
        { id: 'services', label: 'Services', icon: Settings, path: '/dashboard/admin', state: 'services' },
        { id: 'orders', label: 'Orders', icon: Package, path: '/dashboard/admin', state: 'orders' },
        { id: 'users', label: 'Users', icon: Users, path: '/dashboard/admin', state: 'users' }
      ]
    }
  };

  const config = roleConfig[userRole];
  const menuTitle = userRole === 'ADMIN' ? 'MANAGEMENT' : 'MENU';

  return (
    <aside className="w-64 bg-white border-r border-gray-200 flex flex-col flex-shrink-0">
      {/* Logo */}
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center gap-3">
          <div className="bg-blue-600 p-2 rounded-xl">
            <Droplets className="text-white" size={24} />
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">WashWise</h1>
            <p className="text-xs text-gray-500">{config.portalName}</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4">
        <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">
          {menuTitle}
        </p>
        <div className="space-y-1">
          {config.menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = activePage === item.id;
            
            return (
              <button
                key={item.id}
                onClick={() => {
                  if (item.state) {
                    // FIXED ROUTING: We are now actually passing the state along with the route!
                    navigate(item.path, { state: { activeTab: item.state } });
                  } else {
                    navigate(item.path);
                  }
                }}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all ${
                  isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <Icon size={20} />
                <span>{item.label}</span>
              </button>
            );
          })}
        </div>
      </nav>

      {/* User Profile */}
      <div className="p-4 border-t border-gray-200">
        <div className="flex items-center gap-3 mb-4">
          <div className={`w-10 h-10 ${userRole === 'ADMIN' ? 'bg-gray-800' : 'bg-blue-600'} rounded-full flex items-center justify-center`}>
            <UserIcon className="text-white" size={24} />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-semibold text-gray-900 text-sm truncate">
              {user?.fullName || userRole.charAt(0) + userRole.slice(1).toLowerCase()}
            </p>
            <p className="text-xs text-gray-500 truncate">{user?.email}</p>
          </div>
        </div>
        <div className={`inline-block ${config.badgeColor} text-xs px-3 py-1 rounded-full font-medium mb-4`}>
          {config.badgeText}
        </div>
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2 text-red-600 hover:bg-red-50 px-4 py-2 rounded-lg font-medium transition-all"
        >
          <LogOut size={18} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}