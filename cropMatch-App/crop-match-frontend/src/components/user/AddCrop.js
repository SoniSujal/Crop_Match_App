// src/components/farmer/AddCrop.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../utils/constants';
import { useAuth } from '../../context/AuthContext';
import cropService from '../../services/cropService';
import '../../styles/AddCrop.css';

const AddCrop = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    category: 'FRUIT',
    quantity: '',
    price: '',
    unit: 'KILOGRAM',
    region: ''
  });

  const [images, setImages] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
//  const navigate = useNavigate();

  const CROP_UNITS = [
    'KILOGRAM', 'GRAM', 'DOZEN', 'PIECE', 'LITRE',
    'MILLILITRE', 'QUINTAL', 'TONNE', 'BUNDLE', 'BOX',
    'CRATE', 'BAG'
  ];

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleImageChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];

    const validImages = selectedFiles.filter(file =>
      allowedTypes.includes(file.type)
    );

    if (validImages.length !== selectedFiles.length) {
      alert('Only JPG, JPEG, and PNG files are allowed.');
      e.target.value = ''; // clear invalid selection
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

    const cropBlob = {
      ...formData,
//      createdBy: user.id,
      status: true,
      createdOn: new Date().toISOString()
    };

    const form = new FormData();
    form.append('cropDTO', new Blob([JSON.stringify(cropBlob)], { type: 'application/json' }));
    images.forEach((image) => {
      form.append('images', image);
    });

    try {
      setLoading(true);
      await cropService.addCrop(form);
//      for (let pair of form.entries()) {
//        console.log(pair[0], pair[1]);
//      }
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
        <label>
          Name:
          <input name="name" value={formData.name} onChange={handleChange} required />
        </label>

        <label>
          Description:
          <input name="description" value={formData.description} onChange={handleChange} />
        </label>

        <label>
          Category:
          <select name="category" value={formData.category} onChange={handleChange}>
            <option value="FRUIT">Fruit</option>
            <option value="PULSES">Pulses</option>
            <option value="VEGETABLES">Vegetables</option>
          </select>
        </label>

        <label>
          Quantity:
          <input type="number" name="quantity" value={formData.quantity} onChange={handleChange} required />
        </label>

        <label>
          Price:
          <input type="number" name="price" value={formData.price} onChange={handleChange} required />
        </label>

        <label>
          Unit:
          <select name="unit" value={formData.unit} onChange={handleChange}>
            {CROP_UNITS.map((unit) => (
              <option key={unit} value={unit}>{unit.charAt(0) + unit.slice(1).toLowerCase()}</option>
            ))}
          </select>
        </label>

        <label>
          Region:
          <input name="region" value={formData.region} onChange={handleChange} required />
        </label>

        <label>
          Images:
          <input
            type="file"
            name="images"
            accept="image/jpeg, image/jpg, image/png"
            onChange={handleImageChange}
            multiple
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Saving...' : 'Save Crop'}
        </button>
      </form>
    </div>
  );
};

export default AddCrop;
