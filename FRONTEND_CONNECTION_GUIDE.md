# 🔌 Frontend Connection Guide - Cart & Order System

## 📍 Backend Server Info
- **Base URL:** `http://localhost:8080`
- **API Prefix:** `/api`
- **CORS Enabled:** Yes (for `http://localhost:3000`)

---

## 🛒 STEP-BY-STEP: Connecting Cart System

### Step 1️⃣: Add Product to Cart

**When user clicks "Add to Cart" button:**

```javascript
// In your React component (e.g., ProductCard.jsx)
const handleAddToCart = async (productId) => {
  const userId = 1; // Get from your auth context/state
  
  try {
    const response = await fetch(`http://localhost:8080/api/cart/${userId}/items`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        productId: productId,
        quantity: 1
      })
    });

    if (response.ok) {
      const updatedCart = await response.json();
      console.log('✅ Item added to cart:', updatedCart);
      // Update your cart state/context
      // Show success message to user
    }
  } catch (error) {
    console.error('❌ Error adding to cart:', error);
  }
};
```

**What data you'll receive:**
```json
{
  "id": 1,
  "userId": 1,
  "totalAmount": 1500.00,
  "items": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "T-Shirt",
        "price": 500.00,
        "category": "tops",
        "image": "image-url"
      },
      "quantity": 3,
      "price": 500.00
    }
  ],
  "createdAt": "2025-12-08T00:00:00",
  "updatedAt": "2025-12-08T00:00:00"
}
```

---

### Step 2️⃣: Display Cart Items

**When user navigates to cart page:**

```javascript
// In your Cart.jsx component
import { useEffect, useState } from 'react';

const Cart = () => {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const userId = 1; // Get from your auth context

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/cart/${userId}`);
      const data = await response.json();
      setCart(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching cart:', error);
      setLoading(false);
    }
  };

  if (loading) return <div>Loading cart...</div>;

  return (
    <div className="cart-container">
      <h2>Shopping Cart</h2>
      
      {cart?.items?.length === 0 ? (
        <p>Your cart is empty</p>
      ) : (
        <>
          {cart?.items?.map((item) => (
            <div key={item.id} className="cart-item">
              <img src={item.product.image} alt={item.product.name} />
              <h3>{item.product.name}</h3>
              <p>Price: ₱{item.price}</p>
              <p>Quantity: {item.quantity}</p>
              <p>Subtotal: ₱{item.price * item.quantity}</p>
              
              {/* Update quantity buttons */}
              <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>-</button>
              <span>{item.quantity}</span>
              <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
              
              {/* Remove item button */}
              <button onClick={() => removeItem(item.id)}>Remove</button>
            </div>
          ))}
          
          <div className="cart-total">
            <h3>Total: ₱{cart?.totalAmount}</h3>
            <button onClick={() => navigateToCheckout()}>Proceed to Checkout</button>
          </div>
        </>
      )}
    </div>
  );
};
```

---

### Step 3️⃣: Update Cart Item Quantity

```javascript
// In your Cart.jsx component
const updateQuantity = async (itemId, newQuantity) => {
  if (newQuantity < 1) {
    // If quantity is 0, remove the item instead
    removeItem(itemId);
    return;
  }

  try {
    const response = await fetch(
      `http://localhost:8080/api/cart/${userId}/items/${itemId}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          quantity: newQuantity
        })
      }
    );

    if (response.ok) {
      const updatedCart = await response.json();
      setCart(updatedCart); // Update state with new cart data
    }
  } catch (error) {
    console.error('Error updating quantity:', error);
  }
};
```

---

### Step 4️⃣: Remove Item from Cart

```javascript
// In your Cart.jsx component
const removeItem = async (itemId) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/cart/${userId}/items/${itemId}`,
      {
        method: 'DELETE'
      }
    );

    if (response.ok) {
      const updatedCart = await response.json();
      setCart(updatedCart);
    }
  } catch (error) {
    console.error('Error removing item:', error);
  }
};
```

---

## 📦 STEP-BY-STEP: Connecting Order System (Checkout)

### Step 5️⃣: Checkout - Create Order from Cart

**When user submits the checkout form:**

```javascript
// In your Checkout.jsx component
const handleSubmit = async (e) => {
  e.preventDefault();
  const userId = 1; // Get from your auth context

  try {
    const response = await fetch(
      `http://localhost:8080/api/orders/checkout/${userId}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          fullName: form.fullName,
          address: form.address,
          city: form.city,
          postal: form.postal,
          paymentMethod: paymentMethod, // "card" or "cod"
          card: paymentMethod === "card" ? form.card : null
        })
      }
    );

    if (response.ok) {
      const order = await response.json();
      console.log('✅ Order created:', order);
      
      // Redirect to order confirmation page
      navigate(`/order-confirmation/${order.id}`);
      
      // Or show success message
      alert(`Order #${order.id} placed successfully!`);
    } else {
      const error = await response.json();
      console.error('❌ Order failed:', error);
      alert('Failed to place order. Please try again.');
    }
  } catch (error) {
    console.error('Error creating order:', error);
    alert('Network error. Please check your connection.');
  }
};
```

**What data you'll receive:**
```json
{
  "id": 1,
  "userId": 1,
  "orderSummary": "T-Shirt x2, Jeans x1",
  "paymentMethod": "card",
  "total": 1500.00,
  "status": "pending",
  "fullName": "Juan Dela Cruz",
  "address": "123 Main St",
  "city": "Manila",
  "postal": "1000",
  "items": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "T-Shirt",
        "price": 500.00
      },
      "quantity": 2,
      "price": 500.00
    }
  ],
  "createdAt": "2025-12-08T00:00:00"
}
```

---

### Step 6️⃣: View User's Order History

```javascript
// In your OrderHistory.jsx component
const [orders, setOrders] = useState([]);
const userId = 1; // Get from your auth context

useEffect(() => {
  fetchOrders();
}, []);

const fetchOrders = async () => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/orders/user/${userId}`
    );
    const data = await response.json();
    setOrders(data);
  } catch (error) {
    console.error('Error fetching orders:', error);
  }
};

return (
  <div className="order-history">
    <h2>My Orders</h2>
    {orders.map((order) => (
      <div key={order.id} className="order-card">
        <h3>Order #{order.id}</h3>
        <p>Date: {new Date(order.createdAt).toLocaleDateString()}</p>
        <p>Status: {order.status}</p>
        <p>Total: ₱{order.total}</p>
        <p>Items: {order.orderSummary}</p>
        <button onClick={() => viewOrderDetails(order.id)}>View Details</button>
      </div>
    ))}
  </div>
);
```

---

### Step 7️⃣: View Order Details

```javascript
// In your OrderDetails.jsx component
const [order, setOrder] = useState(null);
const orderId = useParams().id; // From URL params

useEffect(() => {
  fetchOrderDetails();
}, [orderId]);

const fetchOrderDetails = async () => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/orders/${orderId}`
    );
    const data = await response.json();
    setOrder(data);
  } catch (error) {
    console.error('Error fetching order:', error);
  }
};

return (
  <div className="order-details">
    <h2>Order #{order?.id}</h2>
    
    <div className="shipping-info">
      <h3>Shipping Information</h3>
      <p>{order?.fullName}</p>
      <p>{order?.address}</p>
      <p>{order?.city}, {order?.postal}</p>
    </div>

    <div className="order-items">
      <h3>Items</h3>
      {order?.items?.map((item) => (
        <div key={item.id}>
          <p>{item.product.name}</p>
          <p>Quantity: {item.quantity}</p>
          <p>Price: ₱{item.price}</p>
        </div>
      ))}
    </div>

    <div className="order-summary">
      <p>Payment: {order?.paymentMethod}</p>
      <p>Status: {order?.status}</p>
      <h3>Total: ₱{order?.total}</h3>
    </div>
  </div>
);
```

---

## 🔄 Complete React Context Example (Recommended)

Create a `CartContext.js` to manage cart state globally:

```javascript
// contexts/CartContext.jsx
import { createContext, useState, useContext, useEffect } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState(null);
  const [userId] = useState(1); // Replace with actual auth user ID

  // Fetch cart on mount
  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/cart/${userId}`);
      const data = await response.json();
      setCart(data);
    } catch (error) {
      console.error('Error fetching cart:', error);
    }
  };

  const addToCart = async (productId, quantity = 1) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/cart/${userId}/items`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ productId, quantity })
        }
      );
      const updatedCart = await response.json();
      setCart(updatedCart);
      return updatedCart;
    } catch (error) {
      console.error('Error adding to cart:', error);
      throw error;
    }
  };

  const updateQuantity = async (itemId, quantity) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/cart/${userId}/items/${itemId}`,
        {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ quantity })
        }
      );
      const updatedCart = await response.json();
      setCart(updatedCart);
    } catch (error) {
      console.error('Error updating cart:', error);
    }
  };

  const removeItem = async (itemId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/cart/${userId}/items/${itemId}`,
        { method: 'DELETE' }
      );
      const updatedCart = await response.json();
      setCart(updatedCart);
    } catch (error) {
      console.error('Error removing item:', error);
    }
  };

  const clearCart = async () => {
    try {
      await fetch(`http://localhost:8080/api/cart/${userId}/clear`, {
        method: 'DELETE'
      });
      await fetchCart();
    } catch (error) {
      console.error('Error clearing cart:', error);
    }
  };

  const checkout = async (checkoutData) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/orders/checkout/${userId}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(checkoutData)
        }
      );
      const order = await response.json();
      await fetchCart(); // Refresh cart (should be empty now)
      return order;
    } catch (error) {
      console.error('Error during checkout:', error);
      throw error;
    }
  };

  const value = {
    cart,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart,
    checkout,
    cartItemCount: cart?.items?.length || 0,
    cartTotal: cart?.totalAmount || 0
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within CartProvider');
  }
  return context;
};
```

**Use in your components:**

```javascript
// In any component
import { useCart } from './contexts/CartContext';

function ProductCard({ product }) {
  const { addToCart } = useCart();

  const handleAddToCart = async () => {
    try {
      await addToCart(product.id, 1);
      alert('Added to cart!');
    } catch (error) {
      alert('Failed to add to cart');
    }
  };

  return (
    <div>
      <h3>{product.name}</h3>
      <p>₱{product.price}</p>
      <button onClick={handleAddToCart}>Add to Cart</button>
    </div>
  );
}

function CartBadge() {
  const { cartItemCount } = useCart();
  return <span className="badge">{cartItemCount}</span>;
}
```

---

## 📊 Complete API Endpoints Summary

### Cart Endpoints
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/cart/{userId}` | Get user's cart |
| POST | `/api/cart/{userId}/items` | Add item to cart |
| PUT | `/api/cart/{userId}/items/{itemId}` | Update item quantity |
| DELETE | `/api/cart/{userId}/items/{itemId}` | Remove item |
| DELETE | `/api/cart/{userId}/clear` | Clear entire cart |
| GET | `/api/cart/{userId}/total` | Get cart total |

### Order Endpoints
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/orders` | Get all orders (admin) |
| GET | `/api/orders/{orderId}` | Get order by ID |
| GET | `/api/orders/user/{userId}` | Get user's orders |
| POST | `/api/orders/checkout/{userId}` | Create order from cart |
| PUT | `/api/orders/{orderId}/status` | Update order status |
| PUT | `/api/orders/{orderId}/cancel` | Cancel order |
| DELETE | `/api/orders/{orderId}` | Delete order |
| GET | `/api/orders/status/{status}` | Get orders by status |

---

## ✅ Quick Testing Checklist

1. ✅ Backend running on `http://localhost:8080`
2. ✅ MySQL database running with `southside_apparel` database
3. ✅ Frontend running on `http://localhost:3000`
4. ✅ CORS configured correctly (already done)
5. ✅ Test add to cart
6. ✅ Test view cart
7. ✅ Test update quantity
8. ✅ Test remove item
9. ✅ Test checkout
10. ✅ Test view orders

Your backend is **ready to receive data** from your React frontend! 🚀
