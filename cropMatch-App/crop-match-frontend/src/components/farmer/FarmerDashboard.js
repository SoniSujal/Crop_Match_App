import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import userService from '../../services/user/userService';
import '../../styles/FarmerDashboard.css';


const FarmerDashboard = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const profileData = await userService.getProfile(user.userId);
      setProfile(profileData);
    } catch (error) {
      setError('Failed to load profile data');
      console.error('Profile fetch error:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading" role="status" aria-live="polite" aria-busy={loading}>
          <div className="spinner" aria-hidden="true"></div>
          <p>Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Farmer Dashboard</h1>
        <p>Welcome back, {user?.username}! Manage your farm and connect with buyers.</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="stats-grid">
        <div className="stat-card farmer-primary">
          <div className="stat-icon">👨‍🌾</div>
          <div className="stat-content">
            <h3>Your Profile</h3>
            <p>Farmer Account</p>
            <Link to="/profile/edit" className="stat-link">Manage Profile →</Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📍</div>
          <div className="stat-content">
            <h3>{profile?.country || 'N/A'}</h3>
            <p>Farm Location</p>
            <p className="stat-subtitle">PIN: {profile?.pincode || 'N/A'}</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📱</div>
          <div className="stat-content">
            <h3>Contact</h3>
            <p>{profile?.mobile || 'N/A'}</p>
            <p className="stat-subtitle">{profile?.email || 'N/A'}</p>
          </div>
        </div>
      </div>

      <div className="quick-actions">
        <h2>Quick Actions</h2>
        <div className="actions-grid">
        <Link to="/add/product" className="stat-link">
          <div className="action-card">
            <div className="action-icon">🌱</div>
            <h3>Add Products</h3>
            <p>List your fresh produce for sale</p>
          </div>
        </Link>

        <Link to="/buyer-requests" className="stat-link">
          <div className="action-card">
            <div className="action-icon">📩</div>
            <h3>Manage Buyer Requests</h3>
            <p>View and respond to buyer offers for your products</p>
          </div>
        </Link>

        <Link to="/selected-requests" className="stat-link">
          <div className="action-card">
            <div className="action-icon">💰</div>
            <h3>Sales Analytics</h3>
            <p>View your sales performance and earnings</p>
          </div>
        </Link>

          <Link to="/profile/edit" className="action-card active">
            <div className="action-icon">⚙️</div>
            <h3>Profile Settings</h3>
            <p>Update your farm and contact information</p>
          </Link>
        </div>
      </div>

      <div className="dashboard-info">
        <h2>Farmer Features</h2>
        <div className="feature-list">
          <div className="feature-item">
            <div className="feature-icon">📋</div>
            <div className="feature-content">
              <h4>Product Management</h4>
              <p>Easily add, edit, and manage your farm produce with photos and descriptions</p>
            </div>
          </div>

          <div className="feature-item">
            <div className="feature-icon">🎯</div>
            <div className="feature-content">
              <h4>Direct Sales</h4>
              <p>Connect directly with buyers and eliminate middlemen for better profits</p>
            </div>
          </div>

          <div className="feature-item">
            <div className="feature-icon">📊</div>
            <div className="feature-content">
              <h4>Market Insights</h4>
              <p>Get real-time market prices and demand analytics for your crops</p>
            </div>
          </div>

          <div className="feature-item">
            <div className="feature-icon">🚚</div>
            <div className="feature-content">
              <h4>Order Management</h4>
              <p>Manage orders, delivery schedules, and customer communications</p>
            </div>
          </div>
        </div>
      </div>

      <div className="farming-tips">
        <h2>Farming Tips</h2>
        <div className="tips-grid">
          <div className="tip-card">
            <div className="tip-icon">🌧️</div>
            <h4>Weather Monitoring</h4>
            <p>Keep track of weather conditions to optimize your farming schedule</p>
          </div>

          <div className="tip-card">
            <div className="tip-icon">🌿</div>
            <h4>Organic Practices</h4>
            <p>Consider organic farming methods to increase product value and market appeal</p>
          </div>

          <div className="tip-card">
            <div className="tip-icon">📱</div>
            <h4>Digital Marketing</h4>
            <p>Use photos and detailed descriptions to showcase the quality of your produce</p>
          </div>
        </div>
      </div>

      <div className="recent-activity">
        <h2>Farm Overview</h2>
        <div className="activity-summary">
          <div className="summary-item">
            <span className="summary-label">Farm Status:</span>
            <span className="summary-value status-active">Active</span>
          </div>
          <div className="summary-item">
            <span className="summary-label">Member Since:</span>
            <span className="summary-value">Recently Joined</span>
          </div>
          <div className="summary-item">
            <span className="summary-label">Profile Completion:</span>
            <span className="summary-value">
              {profile ? '100%' : '80%'} Complete
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FarmerDashboard;