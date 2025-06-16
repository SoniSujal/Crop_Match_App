import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import Slider from 'react-slick';
import cropService from '../../services/farmer/cropService';
import '../../styles/RecommendationDetail.css';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const RecommendationDetail = () => {
  // actual cropId passed via state
   const { index } = useParams();  // index in the URL path
    const location = useLocation();
    const navigate = useNavigate();

    const { cropId } = useParams();
  const [recommendation, setRecommendation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!cropId) {
      setError('Crop ID not found.');
      setLoading(false);
      return;
    }

    const fetchRecommendationDetail = async () => {
      try {
        const res = await cropService.getCropById(cropId);
        if (res.data.status === 'SUCCESS') {
          setRecommendation(res.data.data);
        } else {
          setError(res.data.message || 'Failed to load crop details.');
        }
      } catch (err) {
        setError('Error fetching crop details.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendationDetail();
  }, [cropId]);

  const handleBuyNow = () => {
    navigate(`/buyer/orders/create?cropId=${cropId}`);
  };

  const sliderSettings = {
    dots: recommendation?.imagePaths?.length > 1,  // Show dots only if >1 image
    arrows: recommendation?.imagePaths?.length > 1, // Show arrows only if >1
    infinite: recommendation?.imagePaths?.length > 1, // Infinite only if >1
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    customPaging: (i) => (
      <div className="custom-dot"></div>
    ),
    dotsClass: 'slick-dots custom-dots',
  };

  if (loading) return <p className="loading-text">Loading crop details...</p>;
  if (error) return <p className="error-text">{error}</p>;

  return (
    <div className="recommendation-detail-container">
      <div className="carousel-container">
        <Slider {...sliderSettings}>
          {recommendation?.imagePaths?.length > 0 ? (
            recommendation.imagePaths.map((img, idx) => (
              <div key={idx}>
                <img className="carousel-image" src={`http://localhost:8080${img}`} alt={`Crop ${idx}`} />
              </div>
            ))
          ) : (
            <div>No images available</div>
          )}
        </Slider>
      </div>

      <div className="product-brief">
        <h2 className="product-name">
          {recommendation.name} - {recommendation.sellingQuantity} {recommendation.sellingUnit}
        </h2>
        <div className="product-price">
          ₹{recommendation.price}
        </div>
        <div className="product-mrp">MRP (Incl. of all taxes)</div>
      </div>

      <p className="desc">{recommendation.desc || 'No description available.'}</p>

      <div className="section">
        <h3>Product Information</h3>
        <ul>
          <li><strong>🗂️ Category:</strong> {recommendation.categoryName}</li>
          <li><strong>🌱 Variety:</strong> {recommendation.cropType}</li>
          <li><strong>📍 Region:</strong> {recommendation.region}</li>
          <li><strong>⭐ Quality:</strong> {recommendation.quality}</li>
          <li><strong>🌾 Produced Way:</strong> {recommendation.producedWay}</li>
        </ul>
      </div>

      <div className="section">
        <h3>Availability</h3>
        <ul>
          <li><strong>📦 Stock:</strong> {recommendation.quantity} {recommendation.stockUnit}</li>
          <li><strong>✅ Status:</strong> {recommendation.availabilityStatus}</li>
          <li><strong>📅 Expire Date:</strong> {recommendation.expireMonth}</li>
          <li><strong>📅 Ready Date:</strong> {recommendation.expectedReadyMonth}</li>
        </ul>
      </div>

      <div className="section">
        <h3>Seller</h3>
        <p><strong>👤 Seller Name:</strong> {recommendation.sellerName}</p>
      </div>

      <div className="buy-now-container">
        <button className="buy-now-button" onClick={handleBuyNow}>Buy Now</button>
      </div>
    </div>
  );
};

export default RecommendationDetail;