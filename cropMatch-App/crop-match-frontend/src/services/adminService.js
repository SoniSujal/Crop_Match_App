import api from './api';

const adminService = {
  // Get all farmers
  getAllFarmers: async () => {
    try {
      const response = await api.get('/admin/farmers');
      if (response.data.status === 'SUCCESS') {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch farmers');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to fetch farmers');
    }
  },

  // Get all buyers
  getAllBuyers: async () => {
    try {
      const response = await api.get('/admin/buyers');
      if (response.data.status === 'SUCCESS') {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch buyers');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to fetch buyers');
    }
  },

  // Get user by username
  getUser: async (username) => {
    try {
      const response = await api.get(`/admin/user/${username}`);
      if (response.data.status === 'SUCCESS') {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch user');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to fetch user');
    }
  },

  // Update user
  updateUser: async (username, userData) => {
    try {
      const response = await api.put(`/admin/user/${username}`, userData);
      if (response.data.status === 'SUCCESS') {
        return response.data;
      } else {
        throw new Error(response.data.message || 'Failed to update user');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to update user');
    }
  },

  // Delete user
  deleteUser: async (username) => {
    try {
      const response = await api.delete(`/admin/user/${username}`);
      if (response.data.status === 'SUCCESS') {
        return response.data;
      } else {
        throw new Error(response.data.message || 'Failed to delete user');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to delete user');
    }
  }
};

export default adminService;