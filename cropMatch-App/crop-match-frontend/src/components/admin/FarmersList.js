import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import adminService from '../../services/adminService';
import '../../styles/FarmersList.css';

const FarmersList = () => {
  const [farmers, setFarmers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [deleteLoading, setDeleteLoading] = useState(null);

  useEffect(() => {
    fetchFarmers();
  }, []);

  const fetchFarmers = async () => {
    try {
      const farmersData = await adminService.getAllFarmers();
      setFarmers(farmersData);
    } catch (error) {
      setError('Failed to load farmers');
      console.error('Fetch farmers error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (username) => {
    if (!window.confirm(`Are you sure you want to delete farmer "${username}"? This action cannot be undone.`)) {
      return;
    }

    setDeleteLoading(username);
    try {
      await adminService.deleteUser(username);
      setFarmers(farmers.filter(farmer => farmer.username !== username));
      alert('Farmer deleted successfully');
    } catch (error) {
      alert('Failed to delete farmer: ' + error.message);
    } finally {
      setDeleteLoading(null);
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
          <h3>No farmers found</h3>
          <p>{searchTerm ? 'Try adjusting your search criteria' : 'No farmers have registered yet'}</p>
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
                      <Link
                        to={`/admin/edit-user/${farmer.username}`}
                        className="btn btn-edit"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(farmer.username)}
                        className="btn btn-delete"
                        disabled={deleteLoading === farmer.username}
                      >
                        {deleteLoading === farmer.username ? 'Deleting...' : 'Delete'}
                      </button>
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