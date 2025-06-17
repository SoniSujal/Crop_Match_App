// components/buyer/AddRequest.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/auth/api';
import '../../styles/AddRequest.css';


const AddRequest = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    cropName: '',
    requiredQuantity: '', // changed from quantity
    unit: '',
    region: '',
    expectedPrice: '',
    categoryId: '',
    quality: '',
    producedWay: '',
    needByDate: '',
  });


  const [categories, setCategories] = useState([]);
  const [units, setUnits] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [allCrops, setAllCrops] = useState([]);
  const [cropCategoryMap, setCropCategoryMap] = useState({});
  const [suggestedCrops, setSuggestedCrops] = useState([]);

  useEffect(() => {
    const fetchCropsAndMapping = async () => {
      try {
        const res = await api.get('/buyer/buyerRequest/all-crops-name');
        setAllCrops(res.data);
        const mapRes = await api.get('/buyer/buyerRequest/crop-category-mapping');
        setCropCategoryMap(mapRes.data);
      } catch (err) {
        console.error('Failed to fetch crops or mapping:', err);
      }
    };

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

    fetchCropsAndMapping();
    fetchCategories();
    fetchUnits();
    fetchInitialCrops();
  }, []);

  const handleCategoryChange = (e) => {
    const newCategoryId = e.target.value;
    setFormData({ ...formData, categoryId: newCategoryId, cropName: '' });
    fetchInitialCrops(newCategoryId);
  };

  const handleCropNameChange = (e) => {
    const selectedCrop = e.target.value;
    const autoCategoryId = cropCategoryMap[selectedCrop];
    setFormData({
      ...formData,
      cropName: selectedCrop,
      categoryId: autoCategoryId || formData.categoryId
    });
  };

  const fetchInitialCrops = async (categoryId) => {
    try {
      const params = {};
      if (categoryId && categoryId !== "") {
        params.categoryId = categoryId;
      }
      const res = await api.get('/buyer/buyerRequest/initial-crops', { params });
      setSuggestedCrops(res.data);
    } catch (err) {
      console.error('Failed to fetch initial crops:', err);
    }
  };

  const handleCropTyping = async (e) => {
    const value = e.target.value;
    setFormData({ ...formData, cropName: value });

    if (!value) {
      // If input is cleared, reload initial crops
      fetchInitialCrops();
      return;
    }

    if (value.length < 2) return setSuggestedCrops([]);

    try {
      const res = await api.get('/buyer/buyerRequest/suggest-crops', {
        params: {
          query: value,
          categoryId: formData.categoryId || undefined
        }
      });
      setSuggestedCrops(res.data);
    } catch (err) {
      console.error('Failed to fetch suggestions:', err);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/buyer/buyerRequest/create', formData);
      setSuccess('Request posted successfully!');
      setTimeout(() => navigate('/buyer/buyerRequest'), 1000);
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
            Category*:
            <select
              name="categoryId"
              onChange={handleCategoryChange}
              value={formData.categoryId}
              required
            >
              <option value="">Select Category</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
          </label>

        <label>
          Crop Name*:
          <input
            type="text"
            name="cropName"
            value={formData.cropName}
            onChange={handleCropTyping}
            autoComplete="off"
            required
          />
        </label>

        {suggestedCrops.length > 0 && (
          <ul className="suggestion-list">
            {suggestedCrops.map((crop, index) => (
              <li
                key={index}
                onClick={() => {
                  setFormData({
                    ...formData,
                    cropName: crop.name,
                    categoryId: crop.categoryId,
                  });
                  setSuggestedCrops([]); // Clear suggestion list
                }}
              >
                {crop.name}
              </li>
            ))}
          </ul>
        )}

        <label>
            Required Quantity*:
            <input
              name="requiredQuantity"
              type="number"
              placeholder="e.g., 1000"
              onChange={handleChange}
              required
            />
          </label>

        <label>
            Unit*:
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
        </label>

        <label>
            Quality*:
            <select
              name="quality"
              onChange={handleChange}
              required
            >
              <option value="">Select Quality</option>
              <option value="LOW">Low</option>
              <option value="GOOD">Good</option>
              <option value="BEST">Best</option>
            </select>
        </label>

        <label>
            Cultivation Method*:
            <select
              name="producedWay"
              onChange={handleChange}
              required
            >
              <option value="">Select Method</option>
              <option value="ORGANIC">Organic</option>
              <option value="CHEMICAL">Chemical</option>
              <option value="MIXED">Mixed</option>
            </select>
         </label>

        <label>
            Preferred Region*:
            <input
              name="region"
              placeholder="e.g., Gujarat, Punjab"
              onChange={handleChange}
              required
            />
        </label>

        <label>
            Expected Price*:
            <input
              name="expectedPrice"
              type="number"
              placeholder="e.g., 25.50"
              onChange={handleChange}
              required
            />
        </label>

        <label>
            Need By (Deadline)*:
            <input
              name="needByDate"
              type="date"
              min={new Date().toISOString().split('T')[0]}
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
