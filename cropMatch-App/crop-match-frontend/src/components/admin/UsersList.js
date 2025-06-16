import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import adminService from '../../services/admin/adminService';
import '../../styles/UsersList.css';

const UsersList = () => {
  const [searchParams] = useSearchParams();
  const typeFromUrl = searchParams.get('type');

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [deleteLoading, setDeleteLoading] = useState(null);
  const [filter, setFilter] = useState('active'); // 'active' | 'deleted'
  const [userType, setUserType] = useState(typeFromUrl === 'buyer' ? 'buyer' : 'farmer');

  useEffect(() => {
    fetchUsers();
  }, [filter, userType]);

  useEffect(() => {
    if (typeFromUrl === 'buyer') {
      setUserType('buyer');
    } else if (typeFromUrl === 'farmer') {
      setUserType('farmer');
    }
  }, [typeFromUrl]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      let usersData = [];

      if (userType === 'farmer') {
        usersData = filter === 'active'
          ? await adminService.getAllFarmers()
          : await adminService.getDeletedFarmers();
      } else if (userType === 'buyer') {
        usersData = filter === 'active'
          ? await adminService.getAllBuyers()
          : await adminService.getDeletedBuyers();
      }

      setUsers(usersData);
      setError('');
    } catch (error) {
      setError(`Failed to load ${userType}s`);
      console.error(`Fetch ${userType}s error:`, error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (username) => {
    if (!window.confirm(`Are you sure you want to delete ${userType} "${username}"? This action cannot be undone.`)) {
      return;
    }

    setDeleteLoading(username);
    try {
      await adminService.deleteUser(username);
      setUsers(users.filter(user => user.username !== username));
      alert(`${userType.charAt(0).toUpperCase() + userType.slice(1)} deleted successfully`);
    } catch (error) {
      alert(`Failed to delete ${userType}: ` + error.message);
    } finally {
      setDeleteLoading(null);
    }
  };

  const handleActivate = async (email) => {
    if (!window.confirm(`Are you sure you want to reactivate ${userType} "${email}"?`)) {
      return;
    }

    try {
      await adminService.activateUserByEmail(email);
      alert(`${userType.charAt(0).toUpperCase() + userType.slice(1)} reactivated successfully`);
      fetchUsers();
    } catch (error) {
      alert(`Failed to activate ${userType}: ` + error.message);
    }
  };

  const filteredUsers = users.filter(user =>
    user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.country.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading {userType}s...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Users Management</h1>
        <p>Manage all registered users on the platform</p>
      </div>

      {error && (
        <div className="error-message">
          {error}
          <button onClick={fetchUsers} className="retry-btn">Retry</button>
        </div>
      )}

      <div className="page-controls">
        <div className="control-group">
          <div className="user-type-toggle">
            <button
              className={`toggle-btn ${userType === 'farmer' ? 'active' : ''}`}
              onClick={() => setUserType('farmer')}
            >
              👨‍🌾 Farmers
            </button>
            <button
              className={`toggle-btn ${userType === 'buyer' ? 'active' : ''}`}
              onClick={() => setUserType('buyer')}
            >
              🛒 Buyers
            </button>
          </div>

          <div className="filter-box">
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              className="filter-select"
            >
              <option value="active">Active {userType.charAt(0).toUpperCase() + userType.slice(1)}s</option>
              <option value="deleted">Deleted {userType.charAt(0).toUpperCase() + userType.slice(1)}s</option>
            </select>
          </div>
        </div>

        <div className="search-box">
          <input
            type="text"
            placeholder={`Search ${userType}s by name, email, or country...`}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>

        <div className="page-info">
          <span className="total-count">Total {userType.charAt(0).toUpperCase() + userType.slice(1)}s: {users.length}</span>
          {searchTerm && (
            <span className="filtered-count">Filtered: {filteredUsers.length}</span>
          )}
        </div>
      </div>

      {filteredUsers.length === 0 ? (
        <div className="no-data">
          <h3>
            {searchTerm
              ? `No matching ${userType}s found for "${searchTerm}"`
              : filter === 'active'
              ? `No active ${userType}s found`
              : `No deleted ${userType}s found`}
          </h3>
          <p>{searchTerm ? 'Try adjusting your search criteria' : 'There is no data for this filter'}</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Mobile</th>
                <th>Location</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>
                    <div className="user-info">
                      <span className="username">{user.username}</span>
                      <span className={`user-badge ${userType}-badge`}>
                        {userType.charAt(0).toUpperCase() + userType.slice(1)}
                      </span>
                    </div>
                  </td>
                  <td>{user.email}</td>
                  <td>{user.mobile}</td>
                  <td>
                    <div className="location-info">
                      <span className="country">{user.country}</span>
                      <span className="pincode">PIN: {user.pincode}</span>
                    </div>
                  </td>
                  <td>
                    <div className="action-buttons">
                      {filter === 'deleted' ? (
                        <button
                          className="btn btn-activate"
                          onClick={() => handleActivate(user.email)}
                        >
                          Activate
                        </button>
                      ) : (
                        <>
                          <Link
                            to={`/admin/edit-user/${user.username}`}
                            className="btn btn-edit"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(user.username)}
                            className="btn btn-delete"
                            disabled={deleteLoading === user.username}
                          >
                            {deleteLoading === user.username ? 'Deleting...' : 'Delete'}
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default UsersList;
