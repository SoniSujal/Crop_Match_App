import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import cropService from '../../services/farmer/cropService';
import api from '../../services/auth/api';
import '../../styles/BuyerDashboard.css';


const BuyerDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalOrders: 0,
    activeRequests: 0,
    completedDeals: 0
  });

  const [topRecommendations, setTopRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardStats();
    fetchTopRecommendations();
  }, []);

const fetchTopRecommendations = async () => {
  try {
    const response = await cropService.getTopRecommendations(user.email);
    console.log("Top Recommendations:", response);
    setTopRecommendations(response.data?.data || []);
  } catch (err) {
    console.error("Failed to fetch recommendations", err);
  }
};

  const fetchDashboardStats = async () => {
    try {
      // Mock data for now
      setTimeout(() => {
        setStats({
          totalOrders: 12,
          activeRequests: 3,
          completedDeals: 8
        });
        setLoading(false);
      }, 1000);
    } catch (error) {
      setError('Failed to load dashboard statistics');
      console.error('Dashboard stats error:', error);
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Buyer Dashboard</h1>
        <p>Welcome back, {user?.username}! Here's your buying overview.</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon orders-icon">📦</div>
          <div className="stat-content">
            <h3>{stats.totalOrders}</h3>
            <p>Total Orders</p>
            <Link to="/buyer/orders" className="stat-link">View All Orders →</Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon requests-icon">🔍</div>
          <div className="stat-content">
            <h3>{stats.activeRequests}</h3>
            <p>Active Requests</p>
            <Link to="/buyer/requests" className="stat-link">Manage Requests →</Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon deals-icon">✅</div>
          <div className="stat-content">
            <h3>{stats.completedDeals}</h3>
            <p>Completed Deals</p>
            <p className="stat-subtitle">Successful Purchases</p>
          </div>
        </div>
      </div>

      <div className="quick-actions">
        <h2>Quick Actions</h2>
        <div className="actions-grid">
          <Link to="/buyer/browse" className="action-card">
            <div className="action-icon">🌾</div>
            <h3>Browse Crops</h3>
            <p>Find and purchase crops from farmers</p>
          </Link>

          <Link to="/buyer/requests/create" className="action-card">
            <div className="action-icon">➕</div>
            <h3>Create Request</h3>
            <p>Post a buying request for specific crops</p>
          </Link>

        {/* New tile for All Requests */}
        <Link to="/buyer/requests" className="action-card">
          <div className="action-icon">📄</div>  {/* You can choose any icon */}
          <h3>All Requests</h3>
          <p>Manage all your buying requests</p>
        </Link>

          <Link to="/buyer/orders" className="action-card">
            <div className="action-icon">📋</div>
            <h3>My Orders</h3>
            <p>Track your purchase orders and deliveries</p>
          </Link>

          <Link to="/profile/edit" className="action-card">
            <div className="action-icon">⚙️</div>
            <h3>Profile Settings</h3>
            <p>Update your buyer profile information</p>
          </Link>
        </div>
      </div>

        <div className="top-recommendations">
        <h2>Top Recommendations For You</h2>
     <div className="recommendations-grid">
       {topRecommendations.length > 0 ? (
         topRecommendations.map(rec => (
           <Link
             key={rec.id}
             to={`/buyer/recommendation/${encodeURIComponent(rec.cropId)}`}
             className="recommendation-card action-card"
           >
             <div className="recommendation-image-wrapper">
               {rec.imagePaths?.length > 0 ? (
                 <img
                   src={`http://localhost:8080${rec.imagePaths[0]}`}
                   alt={rec.name}
                   className="recommendation-image"
                 />
               ) : (
                 <div className="no-image-placeholder">No image available</div>
               )}
             </div>
             <h3>{rec.name}</h3>
             <p>{rec.sellerName}</p>
           </Link>
         ))
       ) : (
         <p>No recommendations available at the moment.</p>
       )}


       <div className="recommendations-footer">
         <Link to="/buyer/recommendations" className="view-more-link">
           <div className="circle-arrow">→</div>
           <span>View More</span>
         </Link>
       </div>
     </div>
     </div>

<br/><br/>

      <div className="recent-activity">
        <h2>Recent Activity</h2>
        <div className="activity-list">
          <div className="activity-item">
            <div className="activity-icon">📦</div>
            <div className="activity-content">
              <h4>Order Placed</h4>
              <p>Ordered 50kg wheat from FarmerJohn</p>
              <span className="activity-time">2 hours ago</span>
            </div>
            <div className="activity-status status-pending">Pending</div>
          </div>

          <div className="activity-item">
            <div className="activity-icon">✅</div>
            <div className="activity-content">
              <h4>Deal Completed</h4>
              <p>Successfully purchased 30kg rice from AgriCorp</p>
              <span className="activity-time">1 day ago</span>
            </div>
            <div className="activity-status status-completed">Completed</div>
          </div>

          <div className="activity-item">
            <div className="activity-icon">🔍</div>
            <div className="activity-content">
              <h4>Request Posted</h4>
              <p>Looking for organic tomatoes - 100kg</p>
              <span className="activity-time">3 days ago</span>
            </div>
            <div className="activity-status status-active">Active</div>
          </div>
        </div>
      </div>
<br/>
      <div className="dashboard-summary">
        <h2>Market Overview</h2>
        <div className="summary-cards">
          <div className="summary-card">
            <h4>Available Crops</h4>
            <p className="summary-number">245</p>
            <span className="summary-label">Different varieties</span>
          </div>
          <div className="summary-card">
            <h4>Active Farmers</h4>
            <p className="summary-number">89</p>
            <span className="summary-label">In your area</span>
          </div>
          <div className="summary-card">
            <h4>Avg. Response Time</h4>
            <p className="summary-number">2.5h</p>
            <span className="summary-label">For requests</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BuyerDashboard;