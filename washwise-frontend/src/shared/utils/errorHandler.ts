import { AxiosError } from 'axios';

interface ApiErrorBody {
  message?: string;
  error?: unknown;
}

export const handleApiError = (error: unknown): string => {
  if (error instanceof AxiosError) {
    const body = error.response?.data as ApiErrorBody | undefined;
    if (body?.message) {
      return body.message;
    }
    switch (error.response?.status) {
      case 401:
        return 'Your session has expired. Please log in again.';
      case 403:
        return "You don't have permission to do that.";
      case 404:
        return 'The requested resource was not found.';
      case 409:
        return body?.message ?? 'Conflict with current resource state.';
      case 422:
        return 'Some fields are invalid. Please check your input.';
      case 500:
      case 502:
      case 503:
        return 'Server error. Please try again in a moment.';
    }
    if (error.code === 'ERR_NETWORK') {
      return 'Network error. Please check your connection.';
    }
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'An unexpected error occurred.';
};
