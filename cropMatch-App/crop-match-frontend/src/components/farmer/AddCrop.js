import React, { useState, useEffect } from 'react';
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
    quantity: '',
    price: '',
    stockUnit: 'KILOGRAM',
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
      setFormData({ ...formData, [name]: value });
      setError('');
    };

  const handleImageChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    const validImages = selectedFiles.filter(file => allowedTypes.includes(file.type));

    if (validImages.length !== selectedFiles.length) {
      alert('Only JPG, JPEG, and PNG files are allowed.');
      return;
    }

    setImages(validImages);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!user?.email) {
      setError('User not authenticated');
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
      setError(err.message || 'Failed to save crop');
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

        <label>Quantity:
          <input type="number" name="quantity" value={formData.quantity} onChange={handleChange} required />
        </label>

        <label>Price:
          <input type="number" name="price" value={formData.price} onChange={handleChange} required />
        </label>

        <label>Stock Unit:
          <select name="stockUnit" value={formData.stockUnit} onChange={handleChange} required>
            {UNITS.map(unit => (
              <option key={unit} value={unit}>{unit}</option>
            ))}
          </select>
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
          <input type="month" name="expireMonth" value={formData.expireMonth} onChange={handleChange}  min={getCurrentMonth()} required />
        </label>

        <label>Images:
          <input type="file" name="images" accept="image/jpeg,image/jpg,image/png" onChange={handleImageChange} multiple />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Saving...' : 'Save Crop'}
        </button>
      </form>
    </div>
  );
};

export default AddCrop;
