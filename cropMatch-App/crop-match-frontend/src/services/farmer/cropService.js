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

 // Fetch recommendations list for a user with params (pagination, filters)
   getRecommendations: (userEmail, params) => {
       return api.get(`/buyer/${userEmail}/recommendations`, { params });
     },

   // Fetch crop details by crop_id
   getCropById: (cropId) => {
       return api.get(`/buyer/recommendations/${cropId}`); // This hits backend: api/buyer/recommendations/{cropId}
       console.log(cropId);
   },
};

export default cropService;
