// src/services/common/categoryService.js
import api from '../auth/api';

const categoryService = {
  getActiveCategories: async () => {
    const response = await api.get('/categories/active');
    return response.data;
  }
};

export default categoryService;