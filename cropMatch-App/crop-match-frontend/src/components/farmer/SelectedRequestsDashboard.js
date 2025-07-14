
import React, { useEffect, useState } from 'react';
import api from '../../services/auth/api';
import Toast from '../notify/Toast';
import '../../styles/BuyerRequestDashboard.css'; // reuse existing styles

const SelectedRequestsDashboard = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [toast, setToast] = useState({ message: '', type: '' });

  useEffect(() => {
    fetchAcceptedRequests();
  }, []);

  const fetchAcceptedRequests = async () => {
    setLoading(true);
    try {
      const response = await api.get('/farmer/buyer-requests/selected'); // ✅ fetch only accepted
      setRequests(response.data);
      setError('');
    } catch (err) {
      console.error('Error fetching selected requests:', err);
      setError('Failed to load selected requests');
      showToast('Error loading selected requests.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showToast = (message, type) => {
    setToast({ message, type });
  };

  return (
    <div className="buyer-requests-wrapper">
      <h2>Selected Buyer Requests</h2>

      <div className="status-message-wrapper">
        <p className="status-text custom-message">
          These are buyer requests where you were selected to fulfill the demand.
        </p>
      </div>

      {loading && <p className="status-text">Loading selected requests...</p>}
      {error && <p className="status-text error-text">{error}</p>}

      {!loading && requests.length === 0 ? (
        <p className="status-text">No selected requests yet.</p>
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

export default SelectedRequestsDashboard;
