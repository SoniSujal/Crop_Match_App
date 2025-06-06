import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import '../../styles/Header.css';


const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const getDashboardLink = () => {
    if (user?.role === 'admin') return '/admin/dashboard';
    if (user?.role === 'farmer') return '/farmer/dashboard';
    if (user?.role === 'buyer') return '/buyer/dashboard';
    return '/';
  };

  return (
    <header className="header">
      <div className="header-container">
        <div className="header-left">
          <Link to={getDashboardLink()} className="logo">
            CropMatch
          </Link>
        </div>

        <nav className="header-nav">
          {user?.role === 'admin' && (
            <>
              <Link to="/admin/dashboard" className="nav-link">Dashboard</Link>
              <Link to="/admin/farmers" className="nav-link">Farmers</Link>
              <Link to="/admin/buyers" className="nav-link">Buyers</Link>
            </>
          )}
        </nav>

        <div className="header-right">
          <div className="user-info">
            <span className="user-name">Welcome, {user?.username}</span>
            <span className="user-role">({user?.role})</span>
          </div>
          <div className="user-actions">
            <Link to="/profile/edit" className="profile-link">Profile</Link>
            <button onClick={handleLogout} className="logout-btn">
              Logout
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;