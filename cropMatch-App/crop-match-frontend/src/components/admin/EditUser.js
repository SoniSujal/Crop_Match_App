import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import adminService from '../../services/admin/adminService';
import { VALIDATION_PATTERNS, ERROR_MESSAGES } from '../../utils/constants';
import '../../styles/EditUser.css';

const EditUser = () => {
  const { username } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    mobile: '',
    pincode: '',
    country: '',
    region: ''
  });
  const [originalData, setOriginalData] = useState({});
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchUser();
  }, [username]);

  const fetchUser = async () => {
    try {
      const userData = await adminService.getUser(username);
      const userInfo = {
        username: userData.username,
        email: userData.email,
        mobile: userData.mobile,
        pincode: userData.pincode,
        country: userData.country,
        region: userData.region
      };
      setFormData(userInfo);
      setOriginalData(userInfo);
    } catch (error) {
      setError('Failed to load user data: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const validateField = (name, value) => {
    switch (name) {
      case 'username':
        return value.length < 3 ? 'Username must be at least 3 characters long' : '';
      case 'email':
        return !VALIDATION_PATTERNS.EMAIL.test(value) ? ERROR_MESSAGES.INVALID_EMAIL : '';
      case 'mobile':
        return !VALIDATION_PATTERNS.MOBILE.test(value) ? ERROR_MESSAGES.INVALID_MOBILE : '';
      case 'pincode':
        return !VALIDATION_PATTERNS.PINCODE.test(value) ? ERROR_MESSAGES.INVALID_PINCODE : '';
      case 'country':
        return !VALIDATION_PATTERNS.COUNTRY.test(value) ? ERROR_MESSAGES.INVALID_COUNTRY : '';
      case 'region':
        return !VALIDATION_PATTERNS.REGION.test(value) ? 'Region must be alphabetic and max 50 characters' : '';
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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setErrors({});
    setError('');

    // Validate all fields
    const newErrors = {};
    Object.keys(formData).forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) newErrors[key] = error;
    });

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      setSaving(false);
      return;
    }

    try {
      await adminService.updateUser(username, formData);
      alert('User updated successfully!');
      navigate('/admin/farmers');
    } catch (error) {
      setError('Failed to update user: ' + error.message);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  const hasChanges = () => {
    return JSON.stringify(formData) !== JSON.stringify(originalData);
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading user data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Edit User</h1>
        <p>Update user information for: <strong>{username}</strong></p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="form-container">
        <form onSubmit={handleSubmit} className="edit-form">
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
                readOnly
                style={{ backgroundColor: '#B8B8B8' }}
              />
              {errors.email && <span className="field-error">{errors.email}</span>}
            </div>
          </div>

          <div className="form-row">
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

          <div className="form-group">
            <label htmlFor="region">Region *</label>
            <input
              type="text"
              id="region"
              name="region"
              value={formData.region}
              onChange={handleChange}
              required
              placeholder="Enter region"
            />
            {errors.region && <span className="field-error">{errors.region}</span>}
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={handleCancel}
              className="btn btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving || !hasChanges()}
            >
              {saving ? 'Updating...' : 'Update User'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditUser;