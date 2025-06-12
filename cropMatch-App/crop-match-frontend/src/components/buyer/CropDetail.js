// src/components/CropDetail.js
import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../services/auth/api';
import '../../styles/CropDetail.css';

const CropDetail = () => {
  const { cropId } = useParams();
  const [crop, setCrop] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchCropDetail = async () => {
      try {
        const res = await api.get(`/crop/${cropId}`);
        if (res.data.status === 'SUCCESS') {
          setCrop(res.data.data);
        } else {
          setError(res.data.message || 'Failed to load crop');
        }
      } catch (err) {
        console.error(err);
        setError('Error fetching crop details.');
      } finally {
        setLoading(false);
      }
    };

    fetchCropDetail();
  }, [cropId]);

  if (loading) return <div className="crop-detail-container">Loading crop details...</div>;
  if (error) return <div className="crop-detail-container error">{error}</div>;

  return (
    <div className="crop-detail-container">
      <Link to="/buyer/recommendations" className="back-link">← Back to Recommendations</Link>

      <h1>{crop.name}</h1>
      <p className="seller-name">by {crop.sellerName}</p>

      <div className="image-carousel">
        {crop.imagePaths && crop.imagePaths.length > 0 ? (
          crop.imagePaths.map((img, idx) => (
            <img
              key={idx}
              src={img}
              alt={`Crop image ${idx + 1}`}
              className="crop-image"
            />
          ))
        ) : (
          <div>No images available</div>
        )}
      </div>

      <div className="crop-info-grid">
        <div><strong>Category:</strong> {crop.categoryName}</div>
        <div><strong>Type:</strong> {crop.cropType}</div>
        <div><strong>Quality:</strong> {crop.quality}</div>
        <div><strong>Produced:</strong> {crop.producedWay}</div>
        <div><strong>Availability:</strong> {crop.availabilityStatus}</div>
        <div><strong>Region:</strong> {crop.region}</div>
        <div><strong>Expected Ready Month:</strong> {crop.expectedReadyMonth}</div>
        <div><strong>Expire Month:</strong> {crop.expireMonth}</div>
        <div><strong>Quantity:</strong> {crop.quantity} {crop.stockUnit}</div>
        <div><strong>Price:</strong> ₹{crop.price} per {crop.sellingUnit}</div>
      </div>

      <Link to="/buyer/orders" className="buy-button">Place Order</Link>
    </div>
  );
};

export default CropDetail;
