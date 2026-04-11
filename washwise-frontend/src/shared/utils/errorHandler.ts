export const handleApiError = (error: any) => {
    if (error.response) {
      // Server responded with error
      switch (error.response.status) {
        case 401:
          return 'Unauthorized. Please login again.';
        case 403:
          return 'Access forbidden.';
        case 404:
          return 'Resource not found.';
        case 500:
          return 'Server error. Please try again later.';
        default:
          return error.response.data?.message || 'An error occurred.';
      }
    } else if (error.request) {
      // Request made but no response
      return 'Network error. Please check your connection.';
    } else {
      return 'An unexpected error occurred.';
    }
  };