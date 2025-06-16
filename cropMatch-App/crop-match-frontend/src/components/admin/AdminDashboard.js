import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import adminService from '../../services/admin/adminService';
import '../../styles/AdminDashboard.css';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalFarmers: 0,
    totalBuyers: 0,
    totalUsers: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      const [farmers, buyers] = await Promise.all([
        adminService.getAllFarmers(),
        adminService.getAllBuyers()
      ]);

      setStats({
        totalFarmers: farmers.length,
        totalBuyers: buyers.length,
        totalUsers: farmers.length + buyers.length
      });
    } catch (error) {
      setError('Failed to load dashboard statistics');
      console.error('Dashboard stats error:', error);
    } finally {
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
        <h1>Admin Dashboard</h1>
        <p>Welcome back, {user?.username}! Here's what's happening with your platform.</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon farmers-icon">👨‍🌾</div>
          <div className="stat-content">
            <h3>{stats.totalFarmers}</h3>
            <p>Total Farmers</p>
           <Link to="/admin/users?type=farmer" className="stat-link">View All Farmers →</Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon buyers-icon">🛒</div>
          <div className="stat-content">
            <h3>{stats.totalBuyers}</h3>
            <p>Total Buyers</p>
            <Link to="/admin/users?type=buyer" className="stat-link">View All Buyers →</Link>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon users-icon">👥</div>
          <div className="stat-content">
            <h3>{stats.totalUsers}</h3>
            <p>Total Users</p>
            <p className="stat-subtitle">Platform Members</p>
          </div>
        </div>
      </div>

      <div className="quick-actions">
        <h2>Quick Actions</h2>
        <div className="actions-grid">
                  <Link to="/admin/users" className="action-card">
                    <div className="action-icon">👥</div>
                    <h3>Manage Users</h3>
                    <p>View, edit, and manage all user accounts</p>
                  </Link>

          <Link to="/profile/edit" className="action-card">
            <div className="action-icon">⚙️</div>
            <h3>Profile Settings</h3>
            <p>Update your admin profile information</p>
          </Link>
        </div>
      </div>

      <div className="recent-activity">
        <h2>Platform Overview</h2>
        <div className="activity-summary">
          <div className="summary-item">
            <span className="summary-label">Platform Status:</span>
            <span className="summary-value status-active">Active & Running</span>
          </div>
          <div className="summary-item">
            <span className="summary-label">User Distribution:</span>
            <span className="summary-value">
              {((stats.totalFarmers / stats.totalUsers) * 100).toFixed(1)}% Farmers,
              {((stats.totalBuyers / stats.totalUsers) * 100).toFixed(1)}% Buyers
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;