import React, { useState, useEffect } from 'react';
import categoryService from '../../services/common/categoryService';
import { useAuth } from '../../context/AuthContext';
import '../../styles/AdminCategoryManager.css';

const AdminCategoryManager = () => {
  const { user } = useAuth();
  const [categories, setCategories] = useState([]);
  const [filter, setFilter] = useState('active');
  const [showAddForm, setShowAddForm] = useState(false);
  const [newCategory, setNewCategory] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const loadCategories = async () => {
    try {
      const result =
        filter === 'active'
          ? await categoryService.getActiveCategories()
          : await categoryService.getInactiveCategories();
      setCategories(result);
    } catch (err) {
      console.error(err);
      setError('❌ Failed to load categories');
    }
  };

  const handleToggleStatus = async (id) => {
    try {
      setShowAddForm(false); // Close the add form if open
      await categoryService.toggleCategoryStatus(id);
      showMessage('success', '✅ Category status updated');
      await loadCategories();
    } catch (err) {
      console.error(err);
      showMessage('error', '❌ Failed to update category status');
    }
  };

  const formatCategoryName = (name) => {
    if (!name) return '';
    return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase();
  };

  const handleAddCategory = async () => {
    if (!newCategory.trim()) return;

    const formattedName = formatCategoryName(newCategory.trim());

    try {
      await categoryService.addCategory(formattedName);
      setNewCategory('');
      setShowAddForm(false);
      showMessage('success', 'Category added successfully');
      setFilter('active');
      await loadCategories();
    } catch (err) {
      console.error(err);
      showMessage('error', 'Failed to add category');
    }
  };


  const showMessage = (type, text) => {
    if (type === 'success') setMessage(text);
    if (type === 'error') setError(text);
    setTimeout(() => {
      setMessage('');
      setError('');
    }, 4000);
  };

  useEffect(() => {
    loadCategories();
  }, [filter]);

  return (
    <div className="category-manager">
      <h2 className="page-title">📂 Category Management</h2>

      <div className="header-row">
        <select
          className="filter-dropdown"
          value={filter}
          onChange={(e) => {
              setShowAddForm(false); // Close add form when filter is changed
              setFilter(e.target.value);
          }}
        >
          <option value="active">🟢 Active Categories</option>
          <option value="inactive">🔴 Inactive Categories</option>
        </select>

        <button
          className="add-button"
          onClick={() => setShowAddForm(!showAddForm)}
        >
          ➕ Add Category
        </button>
      </div>

      {showAddForm && (
        <div className="add-form">
          <input
            type="text"
            value={newCategory}
            placeholder="Enter category name"
            onChange={(e) => setNewCategory(e.target.value)}
          />
          <button onClick={handleAddCategory}>✅ Submit</button>
        </div>
      )}

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <div className="table-wrapper">
        <table className="category-table">
          <thead>
            <tr>
              <th>📌 Name</th>
              <th>Status</th>
              <th>📅 Created On</th>
              <th>📅 Updated On</th>
              <th>⚙️ Action</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((cat) => (
              <tr key={cat.id}>
                <td>{cat.name}</td>
                <td>
                  <span className={cat.isActive ? 'badge badge-active' : 'badge badge-inactive'}>
                    {cat.isActive ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td>{new Date(cat.createdOn).toLocaleString()}</td>
                <td>{new Date(cat.updatedOn).toLocaleString()}</td>
                <td>
                  <button
                    className={cat.isActive ? 'deactivate-btn' : 'activate-btn'}
                    onClick={() => handleToggleStatus(cat.id)}
                  >
                    {cat.isActive ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
            {categories.length === 0 && (
              <tr>
                <td colSpan="4" style={{ textAlign: 'center', color: '#777' }}>
                  🚫 No categories found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminCategoryManager;
