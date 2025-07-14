import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../../styles/PaymentPage.css';

const PaymentPage = () => {
  const [processing, setProcessing] = useState(false);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();
  const { state } = useLocation();

  const [cardNumber, setCardNumber] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvv, setCvv] = useState('');

  // Format input functions
  const formatCardNumber = (value) => {
    return value
      .replace(/\D/g, '') // Remove non-digits
      .slice(0, 16) // Max 16 digits
      .replace(/(.{4})/g, '$1 ')
      .trim();
  };

  const formatExpiry = (value) => {
    const digits = value.replace(/\D/g, '').slice(0, 4);
    if (digits.length < 3) return digits;
    return `${digits.slice(0, 2)}/${digits.slice(2)}`;
  };

  const formatCVV = (value) => {
    return value.replace(/\D/g, '').slice(0, 3);
  };

  const handlePayment = () => {
    const cleanCardNumber = cardNumber.replace(/\s/g, '');
    const expiryRegex = /^(0[1-9]|1[0-2])\/\d{2}$/;

    if (
      cleanCardNumber.length !== 16 ||
      !expiryRegex.test(expiry) ||
      cvv.length !== 3
    ) {
      alert('Please enter valid card details');
      return;
    }

    setProcessing(true);
    setTimeout(() => {
      setProcessing(false);
      setSuccess(true);
      setTimeout(() => {
        navigate('/buyer/orders/my-orders');
      }, 3000);
    }, 3000);
  };

  return (
    <div className="payment-page">
      {!processing && !success && (
        <>
          <div className="payment-header">
              <h2>
                Pay for <span className="highlight">{state?.cropName || 'your order'}</span>
              </h2>
              <p className="total-amount">
                <strong>Total Amount:</strong> ₹{state?.totalPrice?.toFixed(2) || '0.00'}
              </p>
            </div>

          <div className="card-preview">
            <div className="chip"></div>
            <div className="card-number">{cardNumber || 'XXXX XXXX XXXX XXXX'}</div>
            <div className="card-footer">
              <div className="expiry">{expiry || 'MM/YY'}</div>
              <div className="cvv-label">CVV: {cvv ? '•••' : '•••'}</div>
            </div>
          </div>

          <div className="card-form">
            <input
              type="text"
              placeholder="Card Number"
              value={cardNumber}
              onChange={e => setCardNumber(formatCardNumber(e.target.value))}
            />
            <input
              type="text"
              placeholder="MM/YY"
              value={expiry}
              onChange={e => setExpiry(formatExpiry(e.target.value))}
            />
            <input
              type="password"
              placeholder="CVV"
              value={cvv}
              onChange={e => setCvv(formatCVV(e.target.value))}
            />
            <button onClick={handlePayment}>Submit Payment</button>
          </div>
        </>
      )}

      {processing && (
        <div className="processing">
          <div className="loader"></div>
          <p>Processing your payment securely...</p>
        </div>
      )}

      {success && (
        <div className="success">
          <div className="checkmark">&#10004;</div>
          <h2>Payment Successful</h2>
          <p>Your order has been placed successfully.</p>
        </div>
      )}
    </div>
  );
};

export default PaymentPage;
