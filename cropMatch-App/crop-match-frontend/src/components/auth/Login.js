import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Toast from '../notify/Toast.js';
import '../../styles/Auth.css';


const Login = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [toast, setToast] = useState(null);
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError(''); // Clear error when user types
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setToast(null);

    try {
      const response = await login(formData);
      const userRole = response.data.role;

      setToast({ message: 'Login successful!', type: 'success' });

      // Redirect based on role
      if (userRole === 'admin') {
        navigate('/admin/dashboard');
      } else if (userRole === 'farmer') {
        navigate('/farmer/dashboard');
      } else if (userRole === 'buyer') {
        navigate('/buyer/dashboard');
      }
    } catch (error) {
      setError(error.message || 'Login failed. Please try again.');
      setTimeout(() => {
        }, 5000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h1>CropMatch</h1>
          <h2>Welcome Back</h2>
          <p>Sign in to your account</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="username">Username or Email</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              placeholder="Enter your username or email"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Enter your password"
            />
          </div>

          <button
            type="submit"
            className="auth-btn"
            disabled={loading}
          >
            {loading ? 'Signing In...' : 'Sign In'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Don't have an account?
            <Link to="/register" className="auth-link"> Sign Up</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;