import api from '../auth/api';

const userService = {
  // Get current user profile
  getProfile: async (userId) => {
    try {
      const response = await api.get(`/user/${userId}/profile`);
      if (response.data.status === 'SUCCESS') {
        return response.data.data;
      } else {
        throw new Error(response.data.message || 'Failed to fetch profile');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to fetch profile');
    }
  },

  // Update current user profile
  updateProfile: async (profileData,userId) => {
    try {
      const response = await api.put(`/user/${userId}/profile`, profileData);
      if (response.data.status === 'SUCCESS') {
        return response.data;
      } else {
        throw new Error(response.data.message || 'Failed to update profile');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to update profile');
    }
  }
};

export default userService;