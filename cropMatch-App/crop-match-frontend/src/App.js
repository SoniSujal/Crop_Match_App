import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/common/Layout';
import ProtectedRoute from './components/common/ProtectedRoute';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import AdminDashboard from './components/admin/AdminDashboard';
import UsersList from './components/admin/UsersList';
import EditUser from './components/admin/EditUser';
import FarmerDashboard from './components/farmer/FarmerDashboard';
import BuyerRequestDashboard from './components/farmer/BuyerRequestDashboard';
import BuyerDashboard from './components/buyer/BuyerDashboard';
import RecommendationList from './components/buyer/RecommendationList';
import RecommendationDetail from './components/buyer/RecommendationDetail';
import RequestResponses from './components/buyer/RequestResponses';
import EditProfile from './components/user/EditProfile';
import AddRequest from './components/buyer/AddRequest';
import AddCrop from './components/farmer/AddCrop';
import AllRequests from './components/buyer/AllRequests';
import AdminCategoryManager from './components/admin/AdminCategoryManager';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Protected Routes */}
            <Route element={<Layout />}>
              {/* Admin Routes */}
              <Route
                path="/admin/:userId/dashboard"
                element={
                  <ProtectedRoute requiredRole="admin">
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                  path="/admin/:userId/users"
                   element={
                   <ProtectedRoute requiredRole="admin">
                      <UsersList />
                      </ProtectedRoute>
                      }
                   />
              <Route
                path="/admin/:userId/edit-user/:username"
                element={
                  <ProtectedRoute requiredRole="admin">
                    <EditUser />
                  </ProtectedRoute>
                }
              />

              {/* User Routes */}
              <Route
                path="/farmer/:userId/dashboard"
                element={
                  <ProtectedRoute requiredRole="farmer">
                    <FarmerDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/buyer/:userId/dashboard"
                element={
                  <ProtectedRoute requiredRole="buyer">
                    <BuyerDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile/:userId/edit"
                element={
                  <ProtectedRoute>
                    <EditProfile />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/add/product"
                element={
                <ProtectedRoute requiredRole="farmer">
                  <AddCrop />
                </ProtectedRoute>
                }
              />
              <Route
                  path="/buyer/buyerRequest/create"
                      element={
                      <ProtectedRoute requiredRole="buyer">
                          <AddRequest />
                      </ProtectedRoute>
                      }
                />
                <Route
                    path="/buyer/buyerRequest"
                         element={
                         <ProtectedRoute requiredRole="buyer">
                         <AllRequests />
                         </ProtectedRoute>
                         }
                 />
                <Route
                    path="/buyer-requests"
                         element={
                         <ProtectedRoute requiredRole="farmer">
                         <BuyerRequestDashboard />
                         </ProtectedRoute>
                         }
                 />
                 <Route
                 path="/buyer/request-responses"
                  element={
                   <ProtectedRoute requiredRole="buyer">
                  <RequestResponses />
                  </ProtectedRoute>
                  }
                  />
                <Route
                    path="/buyer/recommendations"
                          element={
                          <ProtectedRoute requiredRole="buyer">
                          <RecommendationList />
                          </ProtectedRoute>
                          }
                />
                <Route
                    path="/buyer/recommendation/:cropId"
                         element={
                         <ProtectedRoute requiredRole="buyer">
                         <RecommendationDetail />
                         </ProtectedRoute>
                         }
                />
                <Route
                    path="/admin/:userId/categories"
                        element={
                        <ProtectedRoute requiredRole="admin">
                        <AdminCategoryManager />
                        </ProtectedRoute>
                        }
                />
            </Route>

            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;