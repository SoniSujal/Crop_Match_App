// API Configuration
export const API_BASE_URL = 'http://localhost:8080/api';

// User Roles
export const USER_ROLES = {
  ADMIN: 'admin',
  FARMER: 'farmer',
  BUYER: 'buyer'
};

// Route Paths
export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  ADMIN_DASHBOARD: '/admin/dashboard',
  ADMIN_FARMERS: '/admin/farmers',
  ADMIN_BUYERS: '/admin/buyers',
  ADMIN_EDIT_USER: '/admin/edit-user',
  FARMER_DASHBOARD: '/farmer/dashboard',
  BUYER_DASHBOARD: '/buyer/dashboard',
  EDIT_PROFILE: '/profile/edit'
};

// Form Validation Patterns
export const VALIDATION_PATTERNS = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  MOBILE: /^\d{10}$/,
  PINCODE: /^\d{6}$/,
  COUNTRY: /^[A-Za-z\s]{1,30}$/,
  REGION: /^[A-Za-z\s]{1,50}$/,
  PASSWORD_MIN_LENGTH: 8
};

// Error Messages
export const ERROR_MESSAGES = {
  REQUIRED_FIELD: 'This field is required',
  INVALID_EMAIL: 'Please enter a valid email address',
  INVALID_MOBILE: 'Mobile number must be exactly 10 digits',
  INVALID_PINCODE: 'Pincode must be exactly 6 digits',
  INVALID_COUNTRY: 'Country must contain only letters and spaces (max 30 characters)',
  PASSWORD_TOO_SHORT: 'Password must be at least 8 characters long',
  NETWORK_ERROR: 'Network error. Please check your connection and try again.',
  UNAUTHORIZED: 'You are not authorized to access this resource',
  SERVER_ERROR: 'Server error. Please try again later.'
};

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Login successful!',
  REGISTER_SUCCESS: 'Registration successful! Please login.',
  PROFILE_UPDATED: 'Profile updated successfully!',
  USER_UPDATED: 'User updated successfully!',
  USER_DELETED: 'User deleted successfully!'
};

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'authToken',
  CURRENT_USER: 'currentUser'
};