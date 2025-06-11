import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { VALIDATION_PATTERNS, ERROR_MESSAGES } from '../../utils/constants';
import '../../styles/Auth.css';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    mobile: '',
    userType: '',
    pincode: '',
    country: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [selectedPrefs, setSelectedPrefs] = useState([]);
  const [categories, setCategories] = useState([]);

  const { register } = useAuth();
  const navigate = useNavigate();

  // Fetch categories on mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await fetch('/api/categories'); // Replace with your API endpoint
        if (!response.ok) throw new Error('Failed to fetch categories');
        const data = await response.json();
        setCategories(data); // Expecting array of {id, name}
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };
    fetchCategories();
  }, []);

  const validateField = (name, value) => {
    switch (name) {
      case 'username':
        return value.length < 3 ? 'Username must be at least 3 characters long' : '';
      case 'email':
        return !VALIDATION_PATTERNS.EMAIL.test(value) ? ERROR_MESSAGES.INVALID_EMAIL : '';
      case 'password':
        return value.length < VALIDATION_PATTERNS.PASSWORD_MIN_LENGTH ? ERROR_MESSAGES.PASSWORD_TOO_SHORT : '';
      case 'mobile':
        return !VALIDATION_PATTERNS.MOBILE.test(value) ? ERROR_MESSAGES.INVALID_MOBILE : '';
      case 'pincode':
        return !VALIDATION_PATTERNS.PINCODE.test(value) ? ERROR_MESSAGES.INVALID_PINCODE : '';
      case 'country':
        return !VALIDATION_PATTERNS.COUNTRY.test(value) ? ERROR_MESSAGES.INVALID_COUNTRY : '';
      case 'userType':
        return !value ? 'Please select a user type' : '';
      default:
        return '';
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });

    // Clear error for this field and validate
    const error = validateField(name, value);
    setErrors(prev => ({
      ...prev,
      [name]: error
    }));

    // Clear preferences if userType changed and is not BUYER
    if (name === 'userType' && value !== 'BUYER') {
      setSelectedPrefs([]);
      setErrors(prev => ({ ...prev, preferences: '' }));
    }
  };

  // Handle checkbox preferences
  const handlePrefChange = (e) => {
    const { value, checked } = e.target;
    if (checked) {
      setSelectedPrefs(prev => [...prev, value]);
    } else {
      setSelectedPrefs(prev => prev.filter(pref => pref !== value));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});
    setSuccess('');

    // Validate all fields
    const newErrors = {};
    Object.keys(formData).forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) newErrors[key] = error;
    });

    // Validate preferences if userType is BUYER
    if (formData.userType === 'BUYER') {
      if (selectedPrefs.length < 1 || selectedPrefs.length > 5) {
        newErrors.preferences = 'Buyer must select between 1 and 5 preferences.';
      }
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      setLoading(false);
      return;
    }

    try {
      // Include preferences if buyer
      const dataToSend = formData.userType === 'BUYER'
        ? { ...formData, preferenceCategoryIds: selectedPrefs }
        : formData;

      await register(dataToSend);
      setSuccess('Registration successful! Please login with your credentials.');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (error) {
      setErrors({ general: error.message || 'Registration failed. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card register-card">
        <div className="auth-header">
          <h1>CropMatch</h1>
          <h2>Create Account</h2>
          <p>Join our farming community</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {success && <div className="success-message">{success}</div>}
          {errors.general && <div className="error-message">{errors.general}</div>}

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="username">Username *</label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
                required
                placeholder="Enter username"
              />
              {errors.username && <span className="field-error">{errors.username}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="email">Email *</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                placeholder="Enter email address"
              />
              {errors.email && <span className="field-error">{errors.email}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="password">Password *</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
                placeholder="Enter password (min 8 characters)"
              />
              {errors.password && <span className="field-error">{errors.password}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="mobile">Mobile Number *</label>
              <input
                type="tel"
                id="mobile"
                name="mobile"
                value={formData.mobile}
                onChange={handleChange}
                required
                placeholder="10-digit mobile number"
              />
              {errors.mobile && <span className="field-error">{errors.mobile}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="userType">I am a *</label>
            <select
              id="userType"
              name="userType"
              value={formData.userType}
              onChange={handleChange}
              required
            >
              <option value="">Select your role</option>
              <option value="FARMER">Farmer</option>
              <option value="BUYER">Buyer</option>
            </select>
            {errors.userType && <span className="field-error">{errors.userType}</span>}
          </div>

          {/* Show preferences checkboxes ONLY if Buyer is selected */}
          {formData.userType === 'BUYER' && (
            <div className="form-group">
              <label>Preferences *</label>

              {categories.length === 0 ? (
                <p>Loading categories...</p>
              ) : (
                <select
                  onChange={(e) => {
                    const selectedId = parseInt(e.target.value);
                    if (selectedId && !selectedPrefs.includes(selectedId) && selectedPrefs.length < 5) {
                      setSelectedPrefs(prev => [...prev, selectedId]);
                    }
                    e.target.value = ""; // reset dropdown after selection
                  }}
                >
                  <option value="">Select a category</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              )}

              {/* Show selected preferences as tags */}
              <div className="selected-tags" style={{ marginTop: '10px', display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                {selectedPrefs.map(id => {
                  const cat = categories.find(c => c.id === id);
                  return (
                    <span
                      key={id}
                      style={{
                        background: '#eee',
                        padding: '5px 10px',
                        borderRadius: '15px',
                        display: 'inline-flex',
                        alignItems: 'center'
                      }}
                    >
                      {cat?.name}
                      <button
                        type="button"
                        onClick={() => setSelectedPrefs(prev => prev.filter(pref => pref !== id))}
                        style={{
                          marginLeft: '8px',
                          background: 'transparent',
                          border: 'none',
                          color: '#999',
                          fontWeight: 'bold',
                          cursor: 'pointer'
                        }}
                      >
                        ×
                      </button>
                    </span>
                  );
                })}
              </div>

              {errors.preferences && <span className="field-error">{errors.preferences}</span>}
            </div>
          )}

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="pincode">Pincode *</label>
              <input
                type="text"
                id="pincode"
                name="pincode"
                value={formData.pincode}
                onChange={handleChange}
                required
                placeholder="6-digit pincode"
              />
              {errors.pincode && <span className="field-error">{errors.pincode}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="country">Country *</label>
              <input
                type="text"
                id="country"
                name="country"
                value={formData.country}
                onChange={handleChange}
                required
                placeholder="Enter country"
              />
              {errors.country && <span className="field-error">{errors.country}</span>}
            </div>
          </div>

          <button
            type="submit"
            className="auth-btn"
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account?
            <Link to="/login" className="auth-link"> Sign In</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
