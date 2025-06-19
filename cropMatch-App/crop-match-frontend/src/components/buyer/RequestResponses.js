import React, { useEffect, useState } from 'react';
import api from '../../services/auth/api';
import '../../styles/BuyerRequestDashboard.css';

const RequestResponses = () => {
  const [responses, setResponses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchResponses();
  }, []);

  const fetchResponses = async () => {
    try {
      const response = await api.get('/buyer/buyerRequest/status');
      setResponses(response.data);
    } catch (err) {
      console.error('Error fetching responses:', err);
      setError('Failed to load responses');
    } finally {
      setLoading(false);
    }
  };

  const handleFarmerResponse = async (requestId, action) => {
    try {
      await api.post(`/buyer/respond-to-farmer/${requestId}`, { action }); // Adjust API if needed
      setResponses(prev => prev.filter(r => r.id !== requestId));
    } catch (err) {
      console.error('Failed to respond to farmer:', err);
    }
  };

  return (
    <div className="buyer-requests-wrapper">
      <h2>Accepted Responses to Your Crop Requests</h2>

      {loading && <p className="status-text">Loading responses...</p>}
      {error && <p className="status-text error-text">{error}</p>}

      {!loading && responses.length === 0 ? (
        <p className="status-text">No accepted or rejected responses yet.</p>
      ) : (
        <div className="request-cards">
          {responses.map((res) => (
            <div className="request-card fade-in" key={res.id}>
              <div className="top-line">
                <div className="crop-info">
                  <h4>{res.buyerRequest.matchedCropName} ({res.buyerRequest.quantity} {res.buyerRequest.unit})</h4>
                  <span className="category-text">{res.buyerRequest.categoryName}</span>
                </div>
                <div className="price-tag">₹{res.buyerRequest.expectedPrice}</div>
              </div>

              <div className="info-row">
                <p><strong>Region:</strong> {res.buyerRequest.region}</p>
                <p><strong>Preferred Quality:</strong> {res.buyerRequest.quality}</p>
              </div>

              <div className="info-row">
                <p><strong>Produced Way:</strong> {res.buyerRequest.producedWay}</p>
                <p><strong>Seller Name:</strong> {res.farmer.username}</p>
              </div>

              <div className="info-row">
                <p><strong>Responded On: </strong>
                      {res.respondedOn && !isNaN(new Date(res.respondedOn))
                        ? new Date(res.respondedOn).toLocaleString()
                        : 'N/A'}
                    </p>
              </div>

              <div className="card-actions">
                <button className="btn accept-btn" onClick={() => handleFarmerResponse(res.id, "ACCEPTED")}>
                  Accept Farmer
                </button>
                <button className="btn reject-btn" onClick={() => handleFarmerResponse(res.id, "REJECTED")}>
                  Reject Farmer
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RequestResponses;
