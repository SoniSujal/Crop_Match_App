import React, { useEffect, useState } from 'react';
import api from '../../services/auth/api';
import Toast from '../notify/Toast';
import '../../styles/BuyerRequestDashboard.css';

const BuyerRequestsDashboard = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('pending');
  const [toast, setToast] = useState({ message: '', type: '' });

  useEffect(() => {
    fetchRequests(statusFilter);
  }, [statusFilter]);

  const fetchRequests = async (status) => {
  setLoading(true);
    try {
      const response = await api.get(`/farmer/buyer-requests/${status}`);
      setRequests(response.data);
      setError('');
    } catch (err) {
      console.error('Failed to fetch requests:', err);
      setError('Failed to load requests');
      showToast('Failed to load requests.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleResponse = async (requestId, action) => {
      try {
        await api.post(`/farmer/respond/${requestId}`, { action }); // ✅ POST: ACCEPTED / REJECTED

        // Remove handled request from current list
        setRequests(prev => prev.filter(r => r.id !== requestId));

        if (action === 'REJECTED') {
          showToast("Request rejected. You can view it in 'Rejected Requests' section.",'success');
        } else {
          showToast("Request accepted. Buyer will see your offer.",'success');
        }
      } catch (err) {
        console.error('Error responding to request:', err);
        showToast('Something went wrong while responding.', 'error');
      }
    };

    const showToast = (message, type) => {
        setToast({ message, type });
    };

  return (
    <div className="buyer-requests-wrapper">
      <h2>Incoming Buyer Requests</h2>

      <div className="filter-row">
          <label htmlFor="statusFilter">Show: </label>
          <select
            id="statusFilter"
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value)}
          >
            <option value="pending">Pending</option>
            <option value="accepted">Accepted</option>
            <option value="rejected">Rejected</option>
          </select>
      </div>

      {loading && <p className="status-text">Loading requests...</p>}
      {error && <p className="status-text error-text">{error}</p>}

      {!loading && requests.length === 0 ? (
        <p className="status-text">No {statusFilter} requests at the moment.</p>
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

              {statusFilter === 'pending' && (
                <div className="card-actions">
                  <button className="btn accept-btn" onClick={() => handleResponse(req.id, "ACCEPTED")}>
                    Accept
                  </button>
                  <button className="btn reject-btn" onClick={() => handleResponse(req.id, "REJECTED")}>
                    Reject
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
      {toast.message && (
              <Toast message={toast.message} type={toast.type} onClose={() => setToast({ message: '', type: '' })} />
      )}
    </div>
  );
};

export default BuyerRequestsDashboard;
