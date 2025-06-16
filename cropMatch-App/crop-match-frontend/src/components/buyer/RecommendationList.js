import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import CropTile from './CropTile';
import cropService from '../../services/farmer/cropService';
import '../../styles/RecommendationList.css';

const RecommendationList = () => {
  const { user } = useAuth();
  const [recommendations, setRecommendations] = useState([]);
  const [pagination, setPagination] = useState({ pageNo: 0, totalPages: 0 });
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    searchTerm: '',
    sortBy: 'createdOn',
    sortDir: 'desc',
    producedWayFilter: ''
  });

  if (!user?.email || user?.role!="buyer") {
        setError('User not authenticated');
  }

  const fetchRecommendations = async (page = 0) => {
    try {
      const params = {
        pageNo: page,
        pageSize: 5,
        sortBy: filters.sortBy,
        sortDir: filters.sortDir
      };
      const res = await cropService.getRecommendations(user.email, params);
      let content = res.data.content;

      // local filters
      content = content.filter(c => {
        const matchesSearch =
          c.name.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
          c.region.toLowerCase().includes(filters.searchTerm.toLowerCase());
        const matchesProduced = filters.producedWayFilter
          ? c.producedWay === filters.producedWayFilter
          : true;
        return matchesSearch && matchesProduced;
      });

      setRecommendations(content);
      setPagination({ pageNo: res.data.pageable.pageNumber, totalPages: res.data.totalPages });
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    if (user?.email) fetchRecommendations(pagination.pageNo);
  }, [user, filters]);

  return (
    <div className="recommendation-wrapper">
      <h2>Recommended Crops</h2>
      <div className="filter-bar">
        <input placeholder="🔍 Search crop or region" value={filters.searchTerm}
          onChange={e => setFilters(f => ({ ...f, searchTerm: e.target.value }))} />
        <select value={filters.sortBy} onChange={e => setFilters(f => ({ ...f, sortBy: e.target.value }))}>
          <option value="createdOn">Recent</option>
          <option value="price">Price</option>
          <option value="name">Name</option>
          <option value="stockQuantity">Stock Qty</option>
        </select>
        <select value={filters.sortDir} onChange={e => setFilters(f => ({ ...f, sortDir: e.target.value }))}>
          <option value="desc">Descending</option>
          <option value="asc">Ascending</option>
        </select>
        <select value={filters.producedWayFilter} onChange={e => setFilters(f => ({ ...f, producedWayFilter: e.target.value }))}>
          <option value="">All Types</option>
          <option value="ORGANIC">Organic</option>
          <option value="CHEMICAL">Chemical</option>
          <option value="MIXED">Mixed</option>
        </select>
      </div>
      <div className="tile-list fade-in">
        {recommendations.length ? recommendations.map(c =>
          <CropTile key={c.cropId} crop={c} />) : <p>No crops found matching filters.</p>}
      </div>
      <div className="pagination-buttons">
        <button onClick={() => fetchRecommendations(pagination.pageNo - 1)} disabled={pagination.pageNo === 0}>← Previous</button>
        <span>Page {pagination.pageNo + 1} of {pagination.totalPages}</span>
        <button onClick={() => fetchRecommendations(pagination.pageNo + 1)} disabled={pagination.pageNo + 1 >= pagination.totalPages}>Next →</button>
      </div>
    </div>
  );
};

export default RecommendationList;
