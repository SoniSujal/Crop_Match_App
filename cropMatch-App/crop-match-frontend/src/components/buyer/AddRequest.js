// components/buyer/AddRequest.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/auth/api';
import '../../styles/AddRequest.css';


const AddRequest = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    cropName: '',
    quantity: '',
    unit: '',
    region: '',
    expectedPrice: '',
    categoryId: ''
  });

  const [categories, setCategories] = useState([]);
  const [units, setUnits] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await api.get('/categories'); // Make sure this backend route exists
        setCategories(response.data);
      } catch (err) {
        console.error('Failed to fetch categories:', err);
        setError('Failed to load categories');
      }
    };

    const fetchUnits = async () => {
        try {
          const res = await api.get('/buyer/units');
          setUnits(res.data);
        } catch (err) {
          console.error('Failed to load units:', err);
        }
      };

    fetchCategories();
    fetchUnits();
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/buyer/requests/create', formData);
      setSuccess('Request posted successfully!');
      setTimeout(() => navigate('/buyer/requests'), 1000);
    } catch (err) {
      console.error('Submission error:', err);
      setError('Failed to create request');
    }
  };

  return (
    <div className="form-container">
      <h2>Create a Crop Request</h2>

      {error && <p className="error">{error}</p>}
      {success && <p className="success">{success}</p>}

      <form onSubmit={handleSubmit}>
        <input
          name="cropName"
          placeholder="Crop Name"
          onChange={handleChange}
          required
        />

        <select
          name="categoryId"
          onChange={handleChange}
          required
        >
          <option value="">Select Category</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>

        <input
          name="quantity"
          placeholder="Quantity"
          type="number"
          onChange={handleChange}
          required
        />

        <select
          name="unit"
          onChange={handleChange}
          required
        >
          <option value="">Select Unit</option>
          {units.map((unit) => (
            <option key={unit.name} value={unit.name}>
              {unit.displayName}
            </option>
          ))}
        </select>

        <input
          name="region"
          placeholder="Preferred Region"
          onChange={handleChange}
          required
        />

        <input
          name="expectedPrice"
          placeholder="Expected Price"
          type="number"
          onChange={handleChange}
          required
        />

        <button type="submit">Submit Request</button>
      </form>
    </div>
  );
};

export default AddRequest;
