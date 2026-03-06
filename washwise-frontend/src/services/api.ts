import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

// Request interceptor - Add token to EVERY request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          useAuthStore.getState().logout();
          return Promise.reject(error);
        }

        const { data } = await axios.post(
          'http://localhost:8080/api/v1/auth/refresh-token',
          { refreshToken }
        );

        const { accessToken, refreshToken: newRefreshToken } = data.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        useAuthStore.getState().logout();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (email: string, password: string) =>
    api.post('/auth/login', { email, password }),
  
  register: (email: string, password: string, fullName: string) =>
    api.post('/auth/register', { email, password, fullName }),
};

// Profile API
export const profileAPI = {
  getProfile: () =>
    api.get('/profile'),
  
  updateProfile: (data: Record<string, unknown>) =>
    api.put('/profile', data),
  
  uploadProfileImage: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/profile/upload-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  getPublicProfile: (userId: string) =>
    api.get(`/profile/${userId}`),
};

// Services API
export const servicesAPI = {
  getServices: (page: number, size: number) =>
    api.get(`/services?page=${page}&size=${size}`),
  
  getServiceById: (id: string) =>
    api.get(`/services/${id}`),
  
  createService: (data: Record<string, unknown>) =>
    api.post('/services', data),
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