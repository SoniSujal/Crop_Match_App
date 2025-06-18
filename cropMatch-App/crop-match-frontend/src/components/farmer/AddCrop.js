import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../utils/constants';
import { useAuth } from '../../context/AuthContext';
import cropService from '../../services/farmer/cropService';
import categoryService from '../../services/common/categoryService';
import '../../styles/AddCrop.css';

const AddCrop = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    category: '',
    stockQuantity: '',
    price: '',
    stockUnit: 'KILOGRAM',
    sellingQuantity: '',
    sellingUnit: 'KILOGRAM',
    region: '',
    cropType: '',
    quality: 'GOOD',
    producedWay: 'ORGANIC',
    availabilityStatus: 'AVAILABLE_NOW',
    expectedReadyMonth: '',
    expireMonth: ''
  });

  const [images, setImages] = useState([]);
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [imageError, setImageError] = useState('');
  const fileInputRef = useRef(null);
  const [tempValidImages, setTempValidImages] = useState([]);

  const UNITS = [
    'KILOGRAM', 'GRAM', 'DOZEN', 'PIECE', 'LITRE',
    'MILLILITRE', 'QUINTAL', 'TONNE', 'BUNDLE', 'BOX', 'CRATE', 'BAG'
  ];

  const QUALITIES = ['LOW', 'GOOD', 'BEST'];
  const PRODUCED_WAYS = ['ORGANIC', 'CHEMICAL', 'MIXED'];
  const AVAILABILITY = ['AVAILABLE_NOW', 'GROWING_READY_IN_FUTURE'];

  const getCurrentMonth = () => {
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      return `${year}-${month}`;
    };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await categoryService.getActiveCategories();
        setCategories(response);
      } catch (err) {
        console.error('Failed to fetch categories', err);
      }
    };
    fetchCategories();
  }, []);

  const handleChange = (e) => {
      const { name, value } = e.target;

      // For month fields, validate that the selected month is not in the past
      if ((name === 'expectedReadyMonth' || name === 'expireMonth') && value) {
        const currentMonth = getCurrentMonth();
        if (value < currentMonth) {
          setError(`Please select a month that is ${name === 'expectedReadyMonth' ? 'in the future' : 'not in the past'}`);
          return;
        }
      }

      // Validate positive numbers for quantities and price
      if (['stockQuantity', 'sellingQuantity', 'price'].includes(name)) {
        if (value === '' || Number(value) <= 0) {
          setError(`${name === 'stockQuantity' ? 'Stock Quantity' : name === 'sellingQuantity' ? 'Selling Quantity' : 'Price'} must be greater than 0`);
          return;
        } else {
          setError('');
        }
      } else {
        setError('');
      }
      setFormData({ ...formData, [name]: value });
      setError('');
    };

  const handleImageChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];

    const validImages = selectedFiles.filter(file => allowedTypes.includes(file.type));

    const oversized = selectedFiles.find(file => file.size > 1 * 1024 * 1024);
    const totalSize = selectedFiles.reduce((sum, file) => sum + file.size, 0);

    if (validImages.length !== selectedFiles.length) {
      setImageError('Only JPG, JPEG, and PNG files are allowed.');
      setTempValidImages([]);
      return;
    }

    if (oversized) {
      setImageError('Each image must be less than or equal to 1MB.');
      setTempValidImages(validImages); // Save valid ones
      return;
    }

    if (totalSize > 10 * 1024 * 1024) {
      setImageError('Total image size must be less than 10MB.');
      setTempValidImages(validImages);
      return;
    }

    setImages(validImages);
    setImageError('');
    setTempValidImages([]);
  };

  const handleImageErrorOk = () => {
    setImageError('');
    setImages(tempValidImages);       // Only keep valid ones
    setTempValidImages([]);
    if (fileInputRef.current) {
      fileInputRef.current.value = ''; // Reset file input
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!user?.email || user?.role!="farmer") {
      setError('User not authenticated');
      return;
    }

    if (Number(formData.stockQuantity) <= 0) {
      setError('Stock Quantity must be greater than 0');
      return;
    }
    if (Number(formData.sellingQuantity) <= 0) {
      setError('Selling Quantity must be greater than 0');
      return;
    }
    if (Number(formData.price) <= 0) {
      setError('Price must be greater than 0');
      return;
    }

    const currentMonth = getCurrentMonth();
    if (formData.availabilityStatus === 'GROWING_READY_IN_FUTURE' &&
        formData.expectedReadyMonth < currentMonth) {
      setError('Expected ready month must be in the future');
      return;
    }

    if (formData.expireMonth < currentMonth) {
      setError('Expire month must be in the future');
      return;
    }

    const categoryId = parseInt(categories.find(cat => cat.name === formData.category)?.id || 0);

    const cropBlob = {
      ...formData,
      categoryId,
      quantity: parseInt(formData.quantity),
      price: parseFloat(formData.price),
    };

    const form = new FormData();
    form.append('cropDTO', new Blob([JSON.stringify(cropBlob)], { type: 'application/json' }));
    images.forEach(img => form.append('images', img));

    try {
      setLoading(true);
      await cropService.addCrop(form);
      navigate(ROUTES.FARMER_DASHBOARD);
    } catch (err) {
      if (err?.response?.status === 413) {
          setError("Each image must be less than 1MB.");
      } else {
          setError(err.message || 'Failed to save crop');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="add-crop-container">
        <h2 className="form-title">Add New Crop</h2>

        {error && <p className="form-error">{error}</p>}

        <form onSubmit={handleSubmit} className="add-crop-form">
          <label>Name:
            <input name="name" value={formData.name} onChange={handleChange} required />
          </label>

          <label>
            Description:
            <input name="description" value={formData.description} onChange={handleChange} />
          </label>

          <label>Category:
            <select name="category" value={formData.category} onChange={handleChange} required>
              <option value="">Select category</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.name}>
                  {cat.name.charAt(0).toUpperCase() + cat.name.slice(1).toLowerCase()}
                </option>
              ))}
            </select>
          </label>

          <label>Stock Quantity:
            <input
              type="number"
              name="stockQuantity"
              value={formData.stockQuantity}
              onChange={handleChange}
              min="1"
              required
            />
          </label>

          <label>Stock Unit:
            <select name="stockUnit" value={formData.stockUnit} onChange={handleChange} required>
              {UNITS.map(unit => (
                <option key={unit} value={unit}>{unit}</option>
              ))}
            </select>
          </label>

          <label>Price:
            <input
              type="number"
              name="price"
              value={formData.price}
              onChange={handleChange}
              min="1"
              step="0.01"
              required
            />
          </label>

          <label>Selling Quantity:
            <input
              type="number"
              name="sellingQuantity"
              value={formData.sellingQuantity}
              onChange={handleChange}
              min="1"
              required
            />
          </label>

          <label>Selling Unit:
            <select name="sellingUnit" value={formData.sellingUnit} onChange={handleChange} required>
              {UNITS.map(unit => (
                <option key={unit} value={unit}>{unit}</option>
              ))}
            </select>
          </label>

          <label>Region:
            <input name="region" value={formData.region} onChange={handleChange} required />
          </label>

          <label>Crop Type:
            <input name="cropType" value={formData.cropType} onChange={handleChange} />
          </label>

          <label>Quality:
            <select name="quality" value={formData.quality} onChange={handleChange} required>
              {QUALITIES.map(q => (
                <option key={q} value={q}>{q}</option>
              ))}
            </select>
          </label>

          <label>Produced Way:
            <select name="producedWay" value={formData.producedWay} onChange={handleChange} required>
              {PRODUCED_WAYS.map(p => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </label>

          <label>Availability Status:
            <select name="availabilityStatus" value={formData.availabilityStatus} onChange={handleChange} required>
              {AVAILABILITY.map(a => (
                <option key={a} value={a}>{a}</option>
              ))}
            </select>
          </label>

          {formData.availabilityStatus === 'GROWING_READY_IN_FUTURE' && (
            <label>Expected Ready Month:
              <input
                type="month"
                name="expectedReadyMonth"
                value={formData.expectedReadyMonth}
                onChange={handleChange}
                min={getCurrentMonth()}
                required
              />
            </label>
          )}

          <label>Expire Month:
            <input
              type="month"
              name="expireMonth"
              value={formData.expireMonth}
              onChange={handleChange}
              min={getCurrentMonth()}
              required
            />
          </label>

          <label>Images:
            <input type="file" name="images" accept="image/jpeg,image/jpg,image/png" onChange={handleImageChange} multiple ref={fileInputRef}/>
          </label>
          {imageError && (
            <div className="custom-error-popup">
              <div className="custom-error-box">
                <p>{imageError}</p>
                <button onClick={handleImageErrorOk}>OK</button>
              </div>
            </div>
          )}

          <button type="submit" disabled={loading}>
            {loading ? 'Saving...' : 'Save Crop'}
          </button>
        </form>
      </div>
    );
  };

  export default AddCrop;
