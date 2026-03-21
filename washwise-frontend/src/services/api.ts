import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If error is 401 and we haven't tried to refresh yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        
        if (!refreshToken) {
          // No refresh token, logout user
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
          return Promise.reject(error);
        }

        // Try to refresh token
        const { data } = await axios.post(
          'http://localhost:8080/api/v1/auth/refresh-token',
          { refreshToken }
        );

        // Save new tokens
        localStorage.setItem('token', data.data.token);
        if (data.data.refreshToken) {
          localStorage.setItem('refreshToken', data.data.refreshToken);
        }

        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${data.data.token}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed, logout user
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials: { email: string; password: string }) =>
    api.post('/auth/login', credentials),
  
  register: (userData: Record<string, unknown>) =>
    api.post('/auth/register', userData),
  
  logout: () =>
    api.post('/auth/logout'),
};

// Profile API
export const profileAPI = {
  getProfile: () => api.get('/profile'),
  updateProfile: (data: Record<string, unknown>) => api.put('/profile', data),
  uploadProfileImage: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/profile/upload-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  getPublicProfile: (userId: string) => api.get(`/profile/${userId}`),
};

// Orders API
export const ordersAPI = {
  createOrder: (data: Record<string, unknown>) =>
    api.post('/orders', data),
  
  getMyOrders: () =>
    api.get('/orders/my-orders'),
  
  getAllOrders: () =>
    api.get('/orders'),
  
  updateOrder: (id: string, data: Record<string, unknown>) =>
    api.put(`/orders/${id}`, data),
  
  cancelOrder: (id: string) =>
    api.delete(`/orders/${id}`),
};

// Reviews API
export const reviewsAPI = {
  createReview: (data: Record<string, unknown>) =>
    api.post('/reviews', data),
  
  getServiceReviews: (serviceId: string) =>
    api.get(`/reviews/service/${serviceId}`),
  
  getAverageRating: (serviceId: string) =>
    api.get(`/reviews/service/${serviceId}/rating`),
};

export default api;