import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import adminService from '../../services/adminService';
import '../../styles/BuyersList.css';

const BuyersList = () => {
  const [buyers, setBuyers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [deleteLoading, setDeleteLoading] = useState(null);

  useEffect(() => {
    fetchBuyers();
  }, []);

  const fetchBuyers = async () => {
    try {
      const buyersData = await adminService.getAllBuyers();
      setBuyers(buyersData);
    } catch (error) {
      setError('Failed to load buyers');
      console.error('Fetch buyers error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (username) => {
    if (!window.confirm(`Are you sure you want to delete buyer "${username}"? This action cannot be undone.`)) {
      return;
    }

    setDeleteLoading(username);
    try {
      await adminService.deleteUser(username);
      setBuyers(buyers.filter(buyer => buyer.username !== username));
      alert('Buyer deleted successfully');
    } catch (error) {
      alert('Failed to delete buyer: ' + error.message);
    } finally {
      setDeleteLoading(null);
    }
  };

  const filteredBuyers = buyers.filter(buyer =>
    buyer.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    buyer.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    buyer.country.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading buyers...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Buyers Management</h1>
        <p>Manage all registered buyers on the platform</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
          <button onClick={fetchBuyers} className="retry-btn">Retry</button>
        </div>
      )}

      <div className="page-controls">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search buyers by name, email, or country..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
        <div className="page-info">
          <span className="total-count">Total Buyers: {buyers.length}</span>
          {searchTerm && (
            <span className="filtered-count">Filtered: {filteredBuyers.length}</span>
          )}
        </div>
      </div>

      {filteredBuyers.length === 0 ? (
        <div className="no-data">
          <h3>No buyers found</h3>
          <p>{searchTerm ? 'Try adjusting your search criteria' : 'No buyers have registered yet'}</p>
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
              {filteredBuyers.map((buyer) => (
                <tr key={buyer.id}>
                  <td>{buyer.id}</td>
                  <td>
                    <div className="user-info">
                      <span className="username">{buyer.username}</span>
                      <span className="user-badge buyer-badge">Buyer</span>
                    </div>
                  </td>
                  <td>{buyer.email}</td>
                  <td>{buyer.mobile}</td>
                  <td>
                    <div className="location-info">
                      <span className="country">{buyer.country}</span>
                      <span className="pincode">PIN: {buyer.pincode}</span>
                    </div>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <Link
                        to={`/admin/edit-user/${buyer.username}`}
                        className="btn btn-edit"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(buyer.username)}
                        className="btn btn-delete"
                        disabled={deleteLoading === buyer.username}
                      >
                        {deleteLoading === buyer.username ? 'Deleting...' : 'Delete'}
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

export default BuyersList;