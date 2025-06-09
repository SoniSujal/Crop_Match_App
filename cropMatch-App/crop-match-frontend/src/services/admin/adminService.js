import api from '../auth/api';

const adminService = {

   activateUserByEmail: async (email) => {
     const response = await api.put(`/admin/users/email/${encodeURIComponent(email)}/activate`);
     return response.data;
   },

  // Get all active farmers
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

  // Get deleted Farmers
  getDeletedFarmers: async () => {
    try {
      const response = await api.get('/admin/farmers/deleted');
      if (response.data.status === 'SUCCESS') {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch deleted farmers');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to fetch deleted farmers');
    }
  },

  // Get All Farmers
  getAllFarmersIncludingDeleted: async () => {
    try {
      const response = await api.get('/admin/farmers/all');
      if (response.data.status === 'SUCCESS') {
         return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch all farmers');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to fetch all farmers');
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

  // Get deleted Buyers
    getDeletedBuyers: async () => {
      try {
        const response = await api.get('/admin/buyers/deleted');
        if (response.data.status === 'SUCCESS') {
          return response.data.data;
        } else {
          throw new Error(response.data.message || 'Failed to fetch deleted buyers');
        }
      } catch (error) {
        if (error.response?.data?.message) {
          throw new Error(error.response.data.message);
        }
        throw new Error('Failed to fetch deleted buyers');
      }
    },

    // Get All Buyers
    getAllBuyersIncludingDeleted: async () => {
      try {
        const response = await api.get('/admin/buyers/all');
        if (response.data.status === 'SUCCESS') {
           return response.data.data;
        } else {
          throw new Error(response.data.message || 'Failed to fetch all buyers');
        }
      } catch (error) {
        if (error.response?.data?.message) {
          throw new Error(error.response.data.message);
        }
        throw new Error('Failed to fetch all buyers');
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