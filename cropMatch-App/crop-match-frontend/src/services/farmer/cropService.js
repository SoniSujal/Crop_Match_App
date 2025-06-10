// src/services/cropService.js
import api from '../auth/api';

const cropService = {
  addCrop: async (formData) => {
    try {
      const response = await api.post('/crops/add', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data.status === 'SUCCESS') {
        return response.data;
      } else {
        throw new Error(response.data.message || 'Failed to add crop');
      }
    } catch (error) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to add crop. Please try again.');
    }
  },

  // You can add more crop-related services here
};

export default cropService;
