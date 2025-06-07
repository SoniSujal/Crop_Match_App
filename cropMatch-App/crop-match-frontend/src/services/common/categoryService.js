// src/services/categoryService.js
import api from './api';

const categoryService = {
  getActiveCategories: async () => {
    const response = await api.get('/categories/active');
    return response.data;
  }
};

export default categoryService;
