import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

type RetryableConfig = InternalAxiosRequestConfig & { _retry?: boolean };

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

const clearSessionAndRedirect = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('user');
  if (window.location.pathname !== '/login') {
    window.location.href = '/login';
  }
};

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetryableConfig | undefined;

    if (!originalRequest || error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    // Don't try to refresh on the auth endpoints themselves — that creates loops.
    const url = originalRequest.url ?? '';
    if (url.includes('/auth/login') || url.includes('/auth/refresh') || url.includes('/auth/register')) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      clearSessionAndRedirect();
      return Promise.reject(error);
    }

    try {
      const { data } = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken });
      const newAccessToken: string = data?.data?.accessToken;
      const newRefreshToken: string | undefined = data?.data?.refreshToken;

      if (!newAccessToken) {
        throw new Error('Missing access token in refresh response');
      }

      localStorage.setItem('accessToken', newAccessToken);
      if (newRefreshToken) {
        localStorage.setItem('refreshToken', newRefreshToken);
      }

      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
      return api(originalRequest);
    } catch (refreshError) {
      clearSessionAndRedirect();
      return Promise.reject(refreshError);
    }
  },
);

// ---------- API surfaces ----------

export const authAPI = {
  login: (credentials: { email: string; password: string }) =>
    api.post('/auth/login', credentials),

  register: (userData: Record<string, unknown>) =>
    api.post('/auth/register', userData),

  refresh: (refreshToken: string) =>
    api.post('/auth/refresh', { refreshToken }),
};

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
  changePassword: (data: { currentPassword: string; newPassword: string }) =>
    api.put('/profile/change-password', data),
  deleteAccount: () => api.delete('/profile/account'),
};

export const ordersAPI = {
  createOrder: (data: Record<string, unknown>) => api.post('/orders', data),
  getMyOrders: () => api.get('/orders/my-orders'),
  getAllOrders: () => api.get('/orders'),
  updateOrder: (id: string, data: Record<string, unknown>) => api.put(`/orders/${id}`, data),
  cancelOrder: (id: string) => api.delete(`/orders/${id}`),
};

export const reviewsAPI = {
  createReview: (data: Record<string, unknown>) => api.post('/reviews', data),
  getServiceReviews: (serviceId: string) => api.get(`/reviews/service/${serviceId}`),
  getAverageRating: (serviceId: string) => api.get(`/reviews/service/${serviceId}/rating`),
};

export const usersAPI = {
  getAllUsers: () => api.get('/users'),
  updateUserRole: (id: string, role: string) => api.put(`/users/${id}/role`, { role }),
};

export const servicesAPI = {
  getAllServices: () => api.get('/services/active'),
  createService: (data: Record<string, unknown>) => api.post('/services', data),
  updateService: (id: string, data: Record<string, unknown>) => api.put(`/services/${id}`, data),
  deleteService: (id: string) => api.delete(`/services/${id}`),
  uploadImage: (id: string, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/services/${id}/upload-image`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

export default api;
