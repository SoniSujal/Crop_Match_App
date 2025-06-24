
import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../../services/auth/api';
import '../../styles/OrderCreation.css';

const OrderCreation = () => {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const cropId = params.get('cropId');
  const requestId = params.get('requestId');

  const [order, setOrder] = useState(null);
  const [deliveryRegion, setDeliveryRegion] = useState('');
  const [qty, setQty] = useState(1);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await api.get('/buyer/orders/create', { params: { cropId, requestId } });
        setOrder(res.data);
        setQty(1);
      } catch (e) {
        setError('Could not load order details');
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [cropId, requestId]);

  useEffect(() => {
    if (order) setTotal(qty * parseFloat(order.unitPrice));
  }, [qty, order]);

  const onPlaceOrder = async () => {
    if (qty < 1) return setError('Quantity must be ≥ 1');
//    try {
//      await api.post('/buyer/orders/place-order', {
//        cropId: order.cropId,
//        requestFarmId: order.buyerRequestFarmId,
//        quantity: qty,
//        deliveryRegion: deliveryRegion.trim(),
//      });
      navigate('/buyer/orders/payment', {
            state: {
              cropName: order.cropName,
              unitPrice: order.unitPrice,
              quantity: qty,
              totalPrice: total,
            },
          });
//    } catch (e) {
//      setError('Order placement failed');
//    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="error">{error}</p>;
  if (!order) return <p>No order data.</p>;

  return (
    <div className="order-creation">
      <h2>Confirm Your Order</h2>
      {order.imageUrl && <img src={`http://localhost:8080${order.imageUrl}`} alt="Crop" />}
      <h3>{order.cropName}</h3>
      <p><strong>Seller:</strong> {order.sellerName}</p>
      <p><strong>Sold From:</strong> {order.region}</p>
      <p><strong>Quality:</strong> {order.quality}</p>
      <p><strong>Produced Way:</strong> {order.producedWay}</p>
      <p><strong>Unit Price:</strong> ₹{order.unitPrice}</p>
      {order.maxQuantity && <p><strong>Available Stock:</strong> {order.maxQuantity} {order.unit}</p>}

      <div className="quantity">
        <label>Quantity:</label>
        <input
          type="number" value={qty} min="1"
          max={order.maxQuantity || undefined}
          onChange={e => setQty(Math.max(1, Math.min(e.target.value, order.maxQuantity || Infinity)))}
        />
        <span>{order.unit}</span>
      </div>
      <div className="delivery-region">
        <label htmlFor="deliveryRegion">Delivery Region: </label>
        <input
          type="text"
          id="deliveryRegion"
          value={deliveryRegion}
          onChange={(e) => setDeliveryRegion(e.target.value)}
          required
        />
      </div>


      <p><strong>Total Price:</strong> ₹{total.toFixed(2)}</p>

      <button onClick={onPlaceOrder}>Place Order</button>
    </div>
  );
};

export default OrderCreation;

