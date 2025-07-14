
import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { FaRegUserCircle} from 'react-icons/fa';
import { MdOutlineLogout } from "react-icons/md";
import { LuUserPen } from "react-icons/lu";
import { HiMenuAlt3 } from "react-icons/hi"; // New: Hamburger Icon
import { IoCloseSharp } from "react-icons/io5"; // New: Close Icon for drawer
import '../../styles/Header.css';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const dropdownRef = useRef();
  const mobileMenuRef = useRef();

  const handleLogout = async () => {
    try {
      await logout();
      setDropdownOpen(false);
      setMobileMenuOpen(false);
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

  const toggleMobileMenu = () => {
    setMobileMenuOpen((prev) => !prev);
  };

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
      if (mobileMenuRef.current && !mobileMenuRef.current.contains(e.target) && !e.target.closest('.menu-icon-toggle')) {
        setMobileMenuOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    setDropdownOpen(false);
    setMobileMenuOpen(false);
  }, [location]);

  // Determine if the user is on a mobile device based on screen width
  // This is a simple client-side check, CSS media queries are the primary mechanism
  const [isMobileView, setIsMobileView] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth <= 768);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <header className="header">
      <div className="header-container">
        <div className="header-left">
          {/* Hamburger icon for admin only on mobile */}
          {user?.role === 'admin' && isMobileView && (
            <div className="menu-icon-toggle" onClick={toggleMobileMenu}>
              {mobileMenuOpen ? <IoCloseSharp size={28} /> : <HiMenuAlt3 size={28} />}
            </div>
          )}
          <Link to={getDashboardLink()} className="logo">
            CropMatch
          </Link>
        </div>

        {/* Desktop Navigation (hidden on mobile) */}
        <nav className="header-nav desktop-nav">
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
                className={`nav-link ${location.pathname.startsWith('/admin/users') && new URLSearchParams(location.search).get('type') === 'farmer' ? 'active' : ''}`}
              >
                Farmers
              </Link>
              <Link
                to={`/admin/${user.userId}/users?type=buyer`}
                className={`nav-link ${location.pathname.startsWith('/admin/users') && new URLSearchParams(location.search).get('type') === 'buyer' ? 'active' : ''}`}
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

          {/* Conditionally render user dropdown icon for non-admin roles OR on desktop for admin */}
          {(!isMobileView || user?.role !== 'admin') && (
            <div className="user-dropdown" ref={dropdownRef}>
              <FaRegUserCircle className="user-icon" onClick={toggleDropdown} size={28} />

              {dropdownOpen && (
                <div className="dropdown-menu">
                  <Link to={`/profile/${user.userId}/edit`} className="dropdown-item" onClick={() => setDropdownOpen(false)}>
                    <LuUserPen className="dropdown-icon" size={20}/> Edit Profile
                  </Link>
                  <button onClick={handleLogout} className="dropdown-item">
                    <MdOutlineLogout className="dropdown-icon" size={20}/> Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Mobile Navigation Drawer - Only for Admin */}
      {user?.role === 'admin' && (
        <div className={`mobile-nav-drawer ${mobileMenuOpen ? 'open' : ''}`} ref={mobileMenuRef}>
          <div className="drawer-header">
            <Link to={getDashboardLink()} className="logo" onClick={() => setMobileMenuOpen(false)}>
              CropMatch
            </Link>
            <IoCloseSharp size={28} className="close-drawer-icon" onClick={toggleMobileMenu} />
          </div>
          <div className="drawer-nav-links">
            <Link
              to="/admin/dashboard"
              className={`drawer-nav-link ${location.pathname === '/admin/dashboard' ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Dashboard
            </Link>
            <Link
              to="/admin/users?type=farmer"
              className={`drawer-nav-link ${location.pathname.startsWith('/admin/users') && new URLSearchParams(location.search).get('type') === 'farmer' ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Farmers
            </Link>
            <Link
              to="/admin/users?type=buyer"
              className={`drawer-nav-link ${location.pathname.startsWith('/admin/users') && new URLSearchParams(location.search).get('type') === 'buyer' ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Buyers
            </Link>
            {/* Always show profile edit and logout in drawer for admin */}
            <Link to="/profile/edit" className="drawer-nav-link" onClick={() => setMobileMenuOpen(false)}>
              <LuUserPen className="drawer-icon"/> Edit Profile
            </Link>
            <button onClick={handleLogout} className="drawer-nav-link logout-btn">
              <MdOutlineLogout className="drawer-icon"/> Logout
            </button>
          </div>
        </div>
      )}
      {mobileMenuOpen && <div className="drawer-overlay" onClick={toggleMobileMenu}></div>}
    </header>
  );
};

export default Header;