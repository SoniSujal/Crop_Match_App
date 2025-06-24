// components/buyer/AddRequest.js
import React, { useState, useEffect , useRef} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/auth/api';
import '../../styles/AddRequest.css';

const CATEGORY_UNIT_MAP = {
  Vegetables: ['KILOGRAM', 'GRAM', 'QUINTAL', 'TONNE', 'BAG', 'CRATE', 'BUNDLE'],
  Fruits: ['KILOGRAM', 'GRAM', 'DOZEN', 'PIECE', 'BOX', 'CRATE', 'BAG'],
  Grains: ['KILOGRAM', 'GRAM', 'QUINTAL', 'TONNE', 'BAG'],
  'Dairy Products': ['LITRE', 'MILLILITRE', 'PIECE', 'BOX'],
  Oils: ['LITRE', 'MILLILITRE'],
  Flowers: ['BUNDLE', 'PIECE', 'DOZEN'],
  'Herbs / Spices': ['GRAM', 'KILOGRAM', 'BAG'],
  'Nuts / Dry Fruits': ['GRAM', 'KILOGRAM', 'BAG', 'BOX'],
  'Saplings / Nursery Plants': ['PIECE', 'BAG', 'CRATE']
};

const UNIVERSAL_PACKAGE_GUIDELINES = {
  BOX: 'Box usually contains 20-30 pieces.',
  BAG: 'Bag usually contains 10-15 pieces.',
  CRATE: 'Crate usually contains 30-50 pieces.',
  BUNDLE: 'Bundle usually contains 5-10 pieces.'
};

const UNITS = [
  'KILOGRAM', 'GRAM', 'DOZEN', 'PIECE', 'LITRE',
  'MILLILITRE', 'QUINTAL', 'TONNE', 'BUNDLE', 'BOX', 'CRATE', 'BAG'
];


const PACKAGED_UNITS = ['BOX', 'BAG', 'CRATE', 'BUNDLE'];

const AddRequest = () => {
  const navigate = useNavigate();
  const inputRef = useRef(null);
  const [dropdownVisible, setDropdownVisible] = useState(false);

  const [formData, setFormData] = useState({
    cropName: '',
    requiredQuantity: '', // changed from quantity
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
  const [units, setUnits] = useState(UNITS); // Default full list
  const [validUnits, setValidUnits] = useState(UNITS); // Based on category
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [allCrops, setAllCrops] = useState([]);
  const [cropCategoryMap, setCropCategoryMap] = useState({});
  const [suggestedCrops, setSuggestedCrops] = useState([]);
  const [priceError, setPriceError] = useState('');
  const [quantityError, setQuantityError] = useState('');


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
    const selectedCategoryName = categories.find(cat => String(cat.id) === newCategoryId)?.name;
    const unitsForCategory = CATEGORY_UNIT_MAP[selectedCategoryName] || UNITS;

    setValidUnits(unitsForCategory);

    setFormData({ ...formData, categoryId: newCategoryId, cropName: '', unit: '' });
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

  const fetchInitialCrops = async (categoryId = formData.categoryId) => {
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

  const handleCropSelection = (selectedCrop) => {
    // First try to find the crop in the suggestedCrops (which might be filtered by category)
    let selectedCropData = suggestedCrops.find(crop => crop.name === selectedCrop);

    // If not found in suggestions, try to find in allCrops
    if (!selectedCropData) {
      selectedCropData = allCrops.find(crop => crop.name === selectedCrop);
    }

    // If we found crop data, use its categoryId or look up in cropCategoryMap
    const categoryId = selectedCropData?.categoryId || cropCategoryMap[selectedCrop];

    setFormData({
      ...formData,
      cropName: selectedCrop,
      categoryId: categoryId || formData.categoryId // Keep existing if no mapping found
    });

    setDropdownVisible(false);
    setSuggestedCrops([]); // Clear suggestions
    setTimeout(() => inputRef.current?.focus(), 100);
  };

  const toggleDropdown = () => {
      if (!dropdownVisible) {
        fetchInitialCrops();
      }
      setDropdownVisible(!dropdownVisible);
    };

  const handleCropTyping = async (e) => {
    const value = e.target.value;
    setFormData(prev => ({ ...prev, cropName: value }));

    if (!value) {
      fetchInitialCrops(formData.categoryId); // Pass current categoryId
      return;
    }

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
    const { name, value } = e.target;

  // Validate Required Quantity
    if (name === 'requiredQuantity') {
      const quantity = parseFloat(value);
      if (quantity < 0) {
        setQuantityError('Quantity must be positive');
      } else {
        setQuantityError('');
      }
    }

    if (name === 'expectedPrice') {
      const price = parseFloat(value);
      if (price < 0) {
        setPriceError('Price must be a positive value');
      } else {
        setPriceError('');
      }
    }

    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (parseFloat(formData.requiredQuantity) <= 0) {
        setError('Quantity must be a positive number');
        return;
    }

    // Validate price before submission
    if (parseFloat(formData.expectedPrice) < 0) {
      setError('Price must be a positive value');
      return;
    }

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
            <div className="crop-input-container">
              <input
                type="text"
                name="cropName"
                value={formData.cropName}
                onChange={handleCropTyping}
                onClick={toggleDropdown}
                onFocus={() => {
                        if (!formData.cropName) fetchInitialCrops();
                      }}
                onBlur={() => setTimeout(() => setSuggestedCrops([]), 200)}
                autoComplete="off"
                required
                placeholder="Type or select a crop"
                ref={inputRef}
              />
              {dropdownVisible && suggestedCrops.length > 0 && (
                <div className="dropdown-list">
                    <div className="dropdown-header">
                          {formData.categoryId ? "Available Crops (Filtered by Category)" : "Available Crops"}
                    </div>
                  {suggestedCrops.map((crop, index) => (
                    <div
                      key={index}
                        className="dropdown-item"
                        onClick={(e) => {
                          e.preventDefault();
                          handleCropSelection(crop.name);
                        }}
                    >
                      <span>{crop.name}</span>
                      {crop.categoryName && (
                                    <span className="category-tag">{crop.categoryName}</span>
                                  )}
                    </div>
                  ))}
                </div>
              )}
            </div>
        </label>

        <label>
            Required Quantity*:
            <input
              name="requiredQuantity"
              type="number"
              min="0.01"
              step="0.01"
              placeholder="e.g., 1000"
              onChange={handleChange}
              required
            />
            {quantityError && <p className="error-text">{quantityError}</p>}
          </label>

        <label>
            Unit*:
            <select
              name="unit"
              onChange={handleChange}
              value={formData.unit}
              required
            >
              <option value="">Select Unit</option>
              {validUnits.map((unit) => (
                <option key={unit} value={unit}>{unit}</option>
              ))}
            </select>

            {/* Packaging guidelines */}
            {PACKAGED_UNITS.includes(formData.unit) && (
              <p style={{
                backgroundColor: '#fffbcc',
                padding: '5px 8px',
                borderRadius: '3px',
                marginTop: '2px',
                color: '#856404',
                fontWeight: '100',
                fontStyle: 'italic',
                border: '1px solid #ffeeba',
              }}>
                {UNIVERSAL_PACKAGE_GUIDELINES[formData.unit]}
              </p>
            )}
        </label>

        <label>
            Quality*:
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
          Need By (Deadline):
          <input
            name="needByDate"
            type="date"
            min={new Date().toISOString().split('T')[0]}
            value={formData.needByDate}
            onChange={handleChange}
          />
        </label>

        <label>
          Preferred Region:
          <input
            name="region"
            value={formData.region}
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
              min="0"
              step="0.01"
              placeholder="e.g., 25.50"
              onChange={handleChange}
              required
            />
            {priceError && <p className="error-text">{priceError}</p>}
        </label>

        <button type="submit">Submit Request</button>
      </form>
    </div>
  );
};

export default AddRequest;
