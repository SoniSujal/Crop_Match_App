import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import userService from '../../services/userService';
import { VALIDATION_PATTERNS, ERROR_MESSAGES } from '../../utils/constants';
import '../../styles/EditProfile.css';


const EditProfile = () => {
  const { user, updateUser } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    mobile: '',
    pincode: '',
    country: ''
  });
  const [originalData, setOriginalData] = useState({});
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const profile = await userService.getProfile();
      const profileData = {
        username: profile.username,
        email: profile.email,
        mobile: profile.mobile,
        pincode: profile.pincode,
        country: profile.country
      };
      setFormData(profileData);
      setOriginalData(profileData);
    } catch (error) {
      setError('Failed to load profile data: ' + error.message);
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

    // Clear success message when user starts editing
    if (success) setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setErrors({});
    setError('');
    setSuccess('');

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
      await userService.updateProfile(formData);
      setSuccess('Profile updated successfully!');
      setOriginalData(formData);

      // Update user context
      updateUser({
        username: formData.username,
        email: formData.email
      });

      // Auto-redirect after success
      setTimeout(() => {
        navigate(-1);
      }, 2000);
    } catch (error) {
      setError('Failed to update profile: ' + error.message);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  const handleReset = () => {
    setFormData({ ...originalData });
    setErrors({});
    setError('');
    setSuccess('');
  };

  const hasChanges = () => {
    return JSON.stringify(formData) !== JSON.stringify(originalData);
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Edit Profile</h1>
        <p>Update your profile information</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {success && (
        <div className="success-message">
          {success}
        </div>
      )}

      <div className="form-container">
        <div className="profile-info">
          <div className="profile-avatar">
            <div className="avatar-placeholder">
              {user?.username?.charAt(0).toUpperCase()}
            </div>
          </div>
          <div className="profile-details">
            <h3>{user?.username}</h3>
            <p className="user-role">{user?.role}</p>
          </div>
        </div>

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

          <div className="form-actions">
            <button
              type="button"
              onClick={handleCancel}
              className="btn btn-secondary"
            >
              Cancel
            </button>

            {hasChanges() && (
              <button
                type="button"
                onClick={handleReset}
                className="btn btn-outline"
              >
                Reset
              </button>
            )}

            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving || !hasChanges()}
            >
              {saving ? 'Updating...' : 'Update Profile'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditProfile;