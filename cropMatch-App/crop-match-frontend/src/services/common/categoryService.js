// src/services/common/categoryService.js
import api from '../auth/api';

const categoryService = {
  getActiveCategories: async () => {
    const response = await api.get('/categories/active');
    return response.data;
  },

  getAllCategories: async () => {
    const response = await api.get('/categories');
    return response.data;
  },

  addCategory: async (name) => {
    const response = await api.post('/categories', { name });
    return response.data;
  },

  // Get inactive categories
  getInactiveCategories: async () => {
    const response = await api.get('/categories/inactive');
    return response.data;
  },

  toggleCategoryStatus: async (id) => {
    const response = await api.put(`/categories/${id}/status`);
    return response.data;
  }
};

export default categoryService;
