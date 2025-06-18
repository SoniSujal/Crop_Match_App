import api from '../auth/api';

const buyerService = {
  createRequest: async (requestData) => {
    const response = await api.post('/buyer/buyerRequest', requestData);
    return response.data;
  },

  getRequests: async () => {
    const response = await api.get('/buyer/buyerRequest/new');
    return response.data;
  }
};

export default buyerService;
