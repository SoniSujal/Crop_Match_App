import api from '../auth/api';

const buyerService = {
  createRequest: async (requestData) => {
    const response = await api.post('/buyer/requests', requestData);
    return response.data;
  },

  getRequests: async () => {
    const response = await api.get('/buyer/requests/new');
    return response.data;
  }
};

export default buyerService;
