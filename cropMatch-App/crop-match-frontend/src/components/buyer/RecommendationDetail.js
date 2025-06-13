import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Slider from 'react-slick';
import '../../styles/RecommendationDetail.css';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const RecommendationDetail = () => {
  const { cropName } = useParams();
  const [recommendation, setRecommendation] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const mockData = {
      name: 'Tomato',
      Desc: 'Fresh organic tomatoes, hand-picked and grown with love.',
      categoryName: 'Vegetables',
      sellerName: 'FarmerJohn',
      sellingQuantity: 2,
      quantity: 100,
      price: 25.5,
      stockUnit: 'KILOGRAM',
      sellingUnit: 'kg',
      region: 'Punjab',
      expireMonth: 'August 2025',
      cropType: 'Organic',
      quality: 'A',
      producedWay: 'Natural',
      availabilityStatus: 'Available',
      expectedReadyMonth: 'July 2025',
      imagePaths: ['/images/tomato_!.jpg', '/images/tomato_2.webp']
    };

    setTimeout(() => {
      setRecommendation(mockData);
      setLoading(false);
    }, 500);
  }, [cropName]);

  if (loading) return <p className="loading-text">Loading crop details...</p>;

  const sliderSettings = {
    dots: true,
    arrows:true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1
  };

  return (
    <div className="recommendation-detail-container">

      <div className="carousel-container">
        <Slider {...sliderSettings}>
          {recommendation.imagePaths.map((img, index) => (
            <div key={index}>
              <img className="carousel-image" src={img} alt={`Crop ${index}`} />
            </div>
          ))}
        </Slider>
      </div>

      <div className="product-brief">
        <h2 className="product-name">
          {recommendation.name} - {recommendation.sellingQuantity} {recommendation.stockUnit}
        </h2>

        <div className="product-price">
          ₹{recommendation.price} / {recommendation.sellingUnit}
        </div>

        <div className="product-mrp">
          MRP (Incl. of all taxes)
        </div>
      </div>

      <p className="desc">{recommendation.Desc}</p>

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
        <p><strong><span role="img" aria-label="seller">👤</span>Seller Name:</strong> {recommendation.sellerName}</p>
      </div>

      <div className="buy-now-container">
        <button className="buy-now-button">Buy Now</button>
      </div>
    </div>
  );
};

export default RecommendationDetail;
