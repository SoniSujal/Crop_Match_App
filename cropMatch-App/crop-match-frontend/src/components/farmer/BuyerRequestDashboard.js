import React, { useEffect, useState } from 'react';
import api from '../../services/auth/api';
import '../../styles/BuyerRequestDashboard.css';

const BuyerRequestsDashboard = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      const response = await api.get('/buyer-requests');
      setRequests(response.data);
    } catch (err) {
      console.error('Failed to fetch requests:', err);
      setError('Failed to load requests');
    } finally {
      setLoading(false);
    }
  };

  const handleResponse = (requestId, action) => {
    api.post(`/farmer/respond/${requestId}`, { action })
      .then(() => {
        setRequests(prev => prev.filter(r => r.id !== requestId));
      })
      .catch(err => console.error(err));
  };

  return (
    <div className="buyer-requests-wrapper">
      <h2>Incoming Buyer Requests</h2>

      {loading && <p className="status-text">Loading requests...</p>}
      {error && <p className="status-text error-text">{error}</p>}

      {!loading && requests.length === 0 ? (
        <p className="status-text">No new requests at the moment.</p>
      ) : (
        <div className="request-cards">
          {requests.map(req => (
            <div className="request-card fade-in" key={req.id}>
              <div className="top-line">
                <div className="crop-info">
                  <h4>{req.matchedCropName} ({req.quantity} {req.unit})</h4>
                  {req.categoryName && <span className="category-text">{req.categoryName}</span>}
                </div>
                <div className="price-tag">₹{req.expectedPrice}</div>
              </div>

              <div className="info-row">
                <p><strong>Region:</strong> {req.region}</p>
                <p><strong>Preferred Quality:</strong> {req.quality}</p>
              </div>

              <div className="info-row">
                <p><strong>Produced Way:</strong> {req.producedWay}</p>
                <p><strong>Need By:</strong> {new Date(req.needByDate).toLocaleDateString()}</p>
              </div>

              <div className="card-actions">
                <button className="btn accept-btn" onClick={() => handleResponse(req.id, "ACCEPTED")}>
                   Accept
                </button>
                <button className="btn reject-btn" onClick={() => handleResponse(req.id, "REJECTED")}>
                   Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default BuyerRequestsDashboard;
