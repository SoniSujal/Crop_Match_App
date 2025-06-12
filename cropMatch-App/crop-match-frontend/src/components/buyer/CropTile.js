// src/components/CropTile.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/CropTile.css';

const CropTile = ({ crop, cropId }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/buyer/recommendation/${cropId}`);
  };

  return (
    <div className="crop-tile" onClick={handleClick}>
      <div className="tile-content">
        <div className="tile-header">
          <h3 className="crop-name">{crop.name}</h3>
          <span className={`category category-${crop.categoryName.toLowerCase().replace(/\s+/g, '-')}`}>
            {crop.categoryName}
          </span>
        </div>
        <div className="tile-details">
          <span className="price">
            ₹{crop.price} / {crop.sellingUnit}
          </span>
          <span className="variety">
            Variety: {crop.cropType || 'N/A'}
          </span>
          <span className="expire">
            Expires: {crop.expireMonth}
          </span>
        </div>
        <div className="tile-footer">
          <span className="seller">Seller: {crop.sellerName}</span>
          <span className="region">Region: {crop.region}</span>
        </div>
      </div>
    </div>
  );
};

export default CropTile;

