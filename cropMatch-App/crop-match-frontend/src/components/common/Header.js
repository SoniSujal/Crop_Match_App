
import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { FaRegUserCircle} from 'react-icons/fa';
import { MdOutlineLogout } from "react-icons/md";
import { LuUserPen } from "react-icons/lu";
import '../../styles/Header.css';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef();

  const handleLogout = async () => {
    try {
      await logout();
      setDropdownOpen(false);
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const getDashboardLink = () => {
    if (user?.role === 'admin') return `/admin/${user.userId}/dashboard`;
    if (user?.role === 'farmer') return `/farmer/${user.userId}/dashboard`;
    if (user?.role === 'buyer') return `/buyer/${user.userId}/dashboard`;
    return '/';
  };

  const toggleDropdown = () => {
    setDropdownOpen((prev) => !prev);
  };

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    setDropdownOpen(false);
  }, [location]);

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
              <Link
                to={`/admin/${user.userId}/dashboard`}
                className={`nav-link ${location.pathname === `/admin/${user.userId}/dashboard` ? 'active' : ''}`}
              >
                Dashboard
              </Link>
              <Link
                to={`/admin/${user.userId}/users?type=farmer`}
                className={`nav-link ${location.pathname === `/admin/${user.userId}/farmers` ? 'active' : ''}`}
              >
                Farmers
              </Link>
              <Link
                to={`/admin/${user.userId}/users?type=buyer`}
                className={`nav-link ${location.pathname === `/admin/${user.userId}/buyers` ? 'active' : ''}`}
              >
                Buyers
              </Link>
            </>
          )}
        </nav>

        <div className="header-right">
          <div className="user-info-clean">
            <span className="welcome-text">
              👋 Welcome, <strong>{user?.username}</strong>
            </span>
            <span className={`role-tag ${user?.role}`}>{user?.role}</span>
          </div>

          <div className="user-dropdown" ref={dropdownRef}>
            <FaRegUserCircle className="user-icon" onClick={toggleDropdown} size={28} />

            {dropdownOpen && (
              <div className="dropdown-menu">
                <Link to={`/profile/${user.userId}/edit`} className="dropdown-item" onClick={() => setDropdownOpen(false)}>
                  <LuUserPen className="dropdown-icon"  size={20}/> Edit Profile
                </Link>
                <button onClick={handleLogout} className="dropdown-item">
                  <MdOutlineLogout className="dropdown-icon" size={20}/> Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;