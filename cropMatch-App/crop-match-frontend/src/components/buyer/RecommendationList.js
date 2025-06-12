import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import CropTile from './CropTile';
import '../../styles/RecommendationList.css';
import api from '../../services/auth/api';

const RecommendationList = () => {
  const { user } = useAuth();
  const [recommendations, setRecommendations] = useState([]);
  const [pagination, setPagination] = useState({ pageNo: 0, totalPages: 0 });
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('id');
  const [cropTypeFilter, setCropTypeFilter] = useState('');

  const fetchRecommendations = async (page = 0) => {
    try {
      const response = await api.get(`/buyer/${user.email}/recommendations`, {
        params: {
          pageNo: page,
          pageSize: 5,
          sortBy: sortBy,
          sortDir: 'asc',
        },
      });

      const filtered = response.data.content.filter((crop) => {
        const matchSearch = crop.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                            crop.region.toLowerCase().includes(searchTerm.toLowerCase());
        const matchType = cropTypeFilter ? crop.cropType === cropTypeFilter : true;
        return matchSearch && matchType;
      });

      setRecommendations(filtered);
      setPagination({
        pageNo: response.data.pageNo,
        totalPages: response.data.totalPages,
      });
    } catch (error) {
      console.error('Error fetching recommendations:', error);
    }
  };

  useEffect(() => {
    if (user?.email) fetchRecommendations();
  }, [user, searchTerm, sortBy, cropTypeFilter]);

  const handleNext = () => {
    if (pagination.pageNo + 1 < pagination.totalPages) {
      fetchRecommendations(pagination.pageNo + 1);
    }
  };

  const handlePrev = () => {
    if (pagination.pageNo > 0) {
      fetchRecommendations(pagination.pageNo - 1);
    }
  };

  return (
    <div className="recommendation-wrapper">
      <h2>Recommended Crops</h2>

      <div className="filter-bar">
        <input
          type="text"
          placeholder="🔍 Search by crop or region"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <select onChange={(e) => setSortBy(e.target.value)}>
          <option value="id">Sort By: Default</option>
          <option value="price">Price</option>
          <option value="region">Region</option>
          <option value="expectedReadyMonth">Ready Month</option>
        </select>
        <select onChange={(e) => setCropTypeFilter(e.target.value)}>
          <option value="">All Types</option>
          <option value="ORGANIC">Organic</option>
          <option value="CHEMICAL">Chemical</option>
        </select>
      </div>

      <div className="tile-list fade-in">
        {recommendations.map((crop, index) => (
          <CropTile key={index} crop={crop} cropId={crop.id || index} />
        ))}
      </div>

      <div className="pagination-buttons">
        <button onClick={handlePrev} disabled={pagination.pageNo === 0}>← Previous</button>
        <span>Page {pagination.pageNo + 1} of {pagination.totalPages}</span>
        <button onClick={handleNext} disabled={pagination.pageNo + 1 >= pagination.totalPages}>Next →</button>
      </div>
    </div>
  );
};

export default RecommendationList;
