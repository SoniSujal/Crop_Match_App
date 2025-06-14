import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import adminService from '../../services/admin/adminService';
import '../../styles/FarmersList.css';

const FarmersList = () => {
  const [farmers, setFarmers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [deleteLoading, setDeleteLoading] = useState(null);
  const [filter, setFilter] = useState('active'); // 'active' | 'deleted' | 'all'

  useEffect(() => {
    fetchFarmers();
  }, [filter]);

  const fetchFarmers = async () => {
    setLoading(true);
      try {
        let farmersData = [];
        if (filter === 'active') {
          farmersData = await adminService.getAllFarmers();
        } else if (filter === 'deleted') {
          farmersData = await adminService.getDeletedFarmers();
        } else if (filter === 'all') {
          farmersData = await adminService.getAllFarmersIncludingDeleted();
        }
        setFarmers(farmersData);
        setError('');
    } catch (error) {
      setError('Failed to load farmers');
      console.error('Fetch farmers error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (email) => {
    if (!window.confirm(`Are you sure you want to delete farmer "${email}"? This action cannot be undone.`)) {
      return;
    }

    setDeleteLoading(email);
    try {
      await adminService.deleteUser(email);
      setFarmers(farmers.filter(farmer => farmer.email !== email));
      alert('Farmer deleted successfully');
    } catch (error) {
      alert('Failed to delete farmer: ' + error.message);
    } finally {
      setDeleteLoading(null);
    }
  };

  const handleActivate = async (email) => {
    if (!window.confirm(`Are you sure you want to reactivate farmer "${email}"?`)) {
      return;
    }

    try {
      await adminService.activateUserByEmail(email);
      alert('Farmer reactivated successfully');
      fetchFarmers();
    } catch (error) {
      alert('Failed to activate farmer: ' + error.message);
    }
  };

  const filteredFarmers = farmers.filter(farmer =>
    farmer.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    farmer.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    farmer.country.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading farmers...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Farmers Management</h1>
        <p>Manage all registered farmers on the platform</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
          <button onClick={fetchFarmers} className="retry-btn">Retry</button>
        </div>
      )}

      <div className="page-controls">
        <div className="filter-box">
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              className="filter-select"
            >
              <option value="active">Active Farmers</option>
              <option value="deleted">Deleted Farmers</option>
              <option value="all">All Farmers</option>
            </select>
        </div>
        <div className="search-box">
          <input
            type="text"
            placeholder="Search farmers by name, email, or country..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
        <div className="page-info">
          <span className="total-count">Total Farmers: {farmers.length}</span>
          {searchTerm && (
            <span className="filtered-count">Filtered: {filteredFarmers.length}</span>
          )}
        </div>
      </div>

      {filteredFarmers.length === 0 ? (
        <div className="no-data">
          <h3>
            {searchTerm
              ? `No matching farmers found for "${searchTerm}"`
              : filter === 'active'
              ? 'No active farmers found'
              : filter === 'deleted'
              ? 'No deleted farmers found'
              : 'No farmers found'}
          </h3>
          <p>{searchTerm ? 'Try adjusting your search criteria' : 'There is no data for this filter'}</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Mobile</th>
                <th>Location</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredFarmers.map((farmer) => (
                <tr key={farmer.id}>
                  <td>{farmer.id}</td>
                  <td>
                    <div className="user-info">
                      <span className="username">{farmer.username}</span>
                      <span className="user-badge farmer-badge">Farmer</span>
                    </div>
                  </td>
                  <td>{farmer.email}</td>
                  <td>{farmer.mobile}</td>
                  <td>
                    <div className="location-info">
                      <span className="country">{farmer.country}</span>
                      <span className="pincode">PIN: {farmer.pincode}</span>
                    </div>
                  </td>
                  <td>
                    <div className="action-buttons">
                      {filter === 'deleted' ? (
                        <button
                          className="btn btn-activate"
                          onClick={() => handleActivate(farmer.email)}
                        >
                          Activate
                        </button>
                      ) : (
                        <>
                          <Link
                            to={`/admin/edit-user/${farmer.email}`}
                            className="btn btn-edit"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(farmer.email)}
                            className="btn btn-delete"
                            disabled={deleteLoading === farmer.email}
                          >
                            {deleteLoading === farmer.email ? 'Deleting...' : 'Delete'}
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default FarmersList;