// components/Toast.js
import React, { useEffect } from 'react';
import '../../styles/Toast.css';

const Toast = ({ message, type = 'success', onClose }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 3500); // show for 3.5 seconds

    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={`toast-container ${type}`}>
      <p>{message}</p>
    </div>
  );
};

export default Toast;
