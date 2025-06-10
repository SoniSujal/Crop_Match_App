// src/components/buyer/AllRequests.js

import React, { useEffect, useState } from 'react';
import api from '../../services/auth/api';
import '../../styles/AllRequests.css';

const AllRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      const response = await api.get('/buyer/requests');
      setRequests(response.data);
    } catch (err) {
      console.error('Failed to fetch requests:', err);
      setError('Failed to load requests');
    } finally {
      setLoading(false);
    }
  };

  const filteredRequests = requests.filter((req) =>
    req.cropName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    req.region?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    req.status?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    req.categoryName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return <div className="loading">Loading your requests...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="all-requests-container">
      <div className="page-header">
        <h2>All Crop Requests</h2>
        <p>Search and manage your crop buying requests</p>
      </div>

      <div className="page-controls">
        <input
          type="text"
          placeholder="Search by crop, region, status, or category..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <span className="total-count">Total: {filteredRequests.length}</span>
      </div>

      {filteredRequests.length === 0 ? (
        <div className="no-data">
          <h3>No requests found</h3>
          <p>Try adjusting your search criteria</p>
        </div>
      ) : (
        <div className="requests-grid">
          {filteredRequests.map((req) => (
            <div className="request-card" key={req.id}>
              <div className="request-header">
                <div className="crop-title">
                  <span className="crop-label">🌾 Crop:</span> {req.cropName}
                </div>
                <span className={`request-status ${req.status.toLowerCase()}`}>
                  {req.status}
                </span>
              </div>


              {req.categoryName && (
                <p className="category-badge">{req.categoryName}</p>
              )}


              <p><strong>Quantity:</strong> {req.quantity} {req.unit}</p>
              <p><strong>Region:</strong> {req.region}</p>
              <p><strong>Expected Price:</strong> ₹{req.expectedPrice}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AllRequests;
