import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import userService from '../../services/user/userService';
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
    country: '',
    region: ''
  });
  const [originalData, setOriginalData] = useState({});
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Preferences related state
  const [categories, setCategories] = useState([]);
  const [selectedPrefs, setSelectedPrefs] = useState([]);

  useEffect(() => {
    fetchProfile();
    fetchCategories();
  }, []);

  // Fetch user profile data including preferences
  const fetchProfile = async () => {
    try {
      const profile = await userService.getProfile();
      setFormData({
        username: profile.username,
        email: profile.email,
        mobile: profile.mobile,
        pincode: profile.pincode,
        country: profile.country,
        region: profile.region
      });
      setOriginalData({
        username: profile.username,
        email: profile.email,
        mobile: profile.mobile,
        pincode: profile.pincode,
        country: profile.country,
        region: profile.region
      });

      // Load preferences if user is BUYER and preferences exist
      if (user?.role.toUpperCase() === 'BUYER' && Array.isArray(profile.preferenceCategoryIds)) {
        setSelectedPrefs(profile.preferenceCategoryIds);
      }
    } catch (err) {
      setError('Failed to load profile data: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // Fetch category list for preferences dropdown
  const fetchCategories = async () => {
    try {
      const response = await fetch('/api/categories'); // Make sure this endpoint exists
      if (!response.ok) throw new Error('Failed to fetch categories');
      const data = await response.json();
      setCategories(data);
    } catch (err) {
      console.error('Error fetching categories:', err);
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
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    const error = validateField(name, value);
    setErrors(prev => ({
      ...prev,
      [name]: error
    }));

    if (success) setSuccess('');
  };

  // Handle preferences add
  const handlePrefAdd = (e) => {
    const selectedId = parseInt(e.target.value);
    if (
      selectedId &&
      !selectedPrefs.includes(selectedId) &&
      selectedPrefs.length < 5
    ) {
      setSelectedPrefs(prev => [...prev, selectedId]);
      setErrors(prev => ({ ...prev, preferences: '' }));
    }else if (selectedPrefs.length >= 5) {
         setErrors(prev => ({ ...prev, preferences: 'You can select up to 5 preferences only.' }));
       }
    e.target.value = '';
    if (success) setSuccess('');
  };

  // Handle preferences remove
  const handlePrefRemove = (id) => {
    const updatedPrefs = selectedPrefs.filter(pref => pref !== id);
    setSelectedPrefs(updatedPrefs);

    // Real-time validation for empty preferences
    if (user?.role.toUpperCase() === 'BUYER' && updatedPrefs.length === 0) {
      setErrors(prev => ({
        ...prev,
        preferences: 'You must select at least one preference.'
      }));
    } else {
      setErrors(prev => ({
        ...prev,
        preferences: ''
      }));
    }

    if (success) setSuccess('');
  };


  const validatePreferences = () => {
    if (user?.role.toUpperCase() === 'BUYER') {
      if (selectedPrefs.length < 1 || selectedPrefs.length > 5) {
        return 'You must select between 1 and 5 preferences.';
      }
    }
    return '';
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setErrors({});
    setError('');
    setSuccess('');

    const newErrors = {};
    Object.keys(formData).forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) newErrors[key] = error;
    });

    // Validate preferences
    const prefError = validatePreferences();
    if (prefError) newErrors.preferences = prefError;

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      setSaving(false);
      return;
    }

    try {
      const updateData = {
        ...formData,
        ...(user?.role.toUpperCase() === 'BUYER' ? { preferenceCategoryIds: selectedPrefs } : {})
      };

      await userService.updateProfile(updateData);

      setSuccess('Profile updated successfully!');
      setOriginalData(formData);

      updateUser({
        username: formData.username,
        email: formData.email
      });

      setTimeout(() => {
        navigate(-1);
      }, 2000);
    } catch (err) {
      setError('Failed to update profile: ' + err.message);
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
    // Reset prefs too
    fetchProfile();
  };

  const hasChanges = () => {
    // Check if form fields or preferences changed
    const formChanged = JSON.stringify(formData) !== JSON.stringify(originalData);
    const prefsChanged = JSON.stringify(selectedPrefs.sort()) !==
      JSON.stringify((user?.preferenceCategoryIds || []).sort());
    return formChanged || prefsChanged;
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

      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <div className="form-container">
        <div className="profile-info">
          <div className="profile-avatar">
            <div className="avatar-placeholder">
              {user?.username?.charAt(0).toUpperCase()}
            </div>
          </div>
          <div className="profile-details">
            <h3>{user?.username}</h3>
            <p className="user-role">{user?.role.toUpperCase()}</p>
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

          {/* Preferences section only for BUYER */}
          {user?.role.toUpperCase() === 'BUYER' && (
            <div className="form-group">
              <label>Preferences *</label>

              {categories.length === 0 ? (
                <p>Loading categories...</p>
              ) : (
                <select onChange={handlePrefAdd} value="">
                  <option value="">Add a category</option>
                  {categories
                    .filter(cat => !selectedPrefs.includes(cat.id))
                    .map(cat => (
                      <option key={cat.id} value={cat.id}>
                        {cat.name}
                      </option>
                    ))}
                </select>
              )}

              <div
                className="selected-tags"
                style={{ marginTop: '10px', display: 'flex', flexWrap: 'wrap', gap: '8px' }}
              >
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
                        onClick={() => handlePrefRemove(id)}
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
