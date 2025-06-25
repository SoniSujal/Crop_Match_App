import api from '../auth/api';

const adminService = {

   activateUserByEmail: async (userId ,email) => {
     const response = await api.put(`/admin/${userId}/users/email/${encodeURIComponent(email)}/activate`);
     return response.data;
   },

  // Get all active farmers
  getAllFarmers: async (userId) => {
    try {
      const response = await api.get(`/admin/${userId}/farmers`);
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
  getDeletedFarmers: async (userId) => {
    try {
      const response = await api.get(`/admin/${userId}/farmers/deleted`);
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
  getAllFarmersIncludingDeleted: async (userId) => {
    try {
      const response = await api.get(`/admin/${userId}/farmers/all`);
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
  getAllBuyers: async (userId) => {
    try {
      const response = await api.get(`/admin/${userId}/buyers`);
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
    getDeletedBuyers: async (userId) => {
      try {
        const response = await api.get(`/admin/${userId}/buyers/deleted`);
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
    getAllBuyersIncludingDeleted: async (userId) => {
      try {
        const response = await api.get(`/admin/${userId}/buyers/all`);
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
  getUser: async (username,userId) => {
    try {
      const response = await api.get(`/admin/${userId}/user/${username}`);
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
  updateUser: async (username, userData, userId) => {
    try {
      const response = await api.put(`/admin/${userId}/user/${username}`, userData);
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
  deleteUser: async (username, userId) => {
    try {
      const response = await api.delete(`/admin/${userId}/user/${username}`);
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