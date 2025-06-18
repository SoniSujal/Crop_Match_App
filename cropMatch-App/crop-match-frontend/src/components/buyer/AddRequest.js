// components/buyer/AddRequest.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/auth/api';
import '../../styles/AddRequest.css';


const AddRequest = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    cropName: '',
    required_quantity: '',
    unit: '',
    region: '',
    expectedPrice: '',
    categoryId: '',
    quality: 'LOW',
    producedWay: 'ORGANIC',
    needByDate: ''
  });

  const QUALITIES = ['LOW', 'GOOD', 'BEST'];
  const PRODUCED_WAYS = ['ORGANIC', 'CHEMICAL', 'MIXED'];
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
        <label>
          Crop Name:
          <input
            name="cropName"
            value={formData.cropName}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Category:
          <select name="categoryId" value={formData.categoryId} onChange={handleChange} required>
            <option value="">Select Category</option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </label>

        <label>
          Required Quantity:
          <input
            name="required_quantity"
            type="number"
            value={formData.required_quantity}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Unit:
          <select name="unit" value={formData.unit} onChange={handleChange} required>
            <option value="">Select Unit</option>
            {units.map((unit) => (
              <option key={unit.name} value={unit.name}>{unit.displayName}</option>
            ))}
          </select>
        </label>

        <label>
          Quality:
          <select name="quality" value={formData.quality} onChange={handleChange} required>
            {QUALITIES.map(q => (
              <option key={q} value={q}>{q}</option>
            ))}
          </select>
        </label>

        <label>
          Produced Way:
          <select
            name="producedWay"
            value={formData.producedWay}
            onChange={handleChange}
            required
          >

            {PRODUCED_WAYS.map(p => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </label>

        <label>
          Need By:
          <input
            name="needByDate"
            type="date"
            min={new Date().toISOString().split('T')[0]}
            value={formData.needByDate}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Preferred Region:
          <input
            name="region"
            value={formData.region}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Expected Price:
          <input
            name="expectedPrice"
            type="number"
            value={formData.expectedPrice}
            onChange={handleChange}
            required
          />
        </label>

        <button type="submit">Submit Request</button>
      </form>
    </div>
  );
};

export default AddRequest;
