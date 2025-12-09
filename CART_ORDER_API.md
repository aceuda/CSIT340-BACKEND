# SouthSide Apparel - Cart & Order API Documentation

## Base URL
```
http://localhost:8080/api
```

---

## 🛒 CART ENDPOINTS

### 1. Get User's Cart
**GET** `/cart/{userId}`

**Description:** Get or create a cart for the user

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "totalAmount": 2500.00,
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
      "quantity": 5,
      "price": 500.00
    }
  ],
  "createdAt": "2025-12-08T00:00:00",
  "updatedAt": "2025-12-08T00:00:00"
}
```

---

### 2. Add Item to Cart
**POST** `/cart/{userId}/items`

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response:** Returns updated cart with all items

---

### 3. Update Cart Item Quantity
**PUT** `/cart/{userId}/items/{itemId}`

**Request Body:**
```json
{
  "quantity": 3
}
```

**Note:** If quantity is 0 or negative, item will be removed

**Response:** Returns updated cart

---

### 4. Remove Item from Cart
**DELETE** `/cart/{userId}/items/{itemId}`

**Response:** Returns updated cart

---

### 5. Clear Cart
**DELETE** `/cart/{userId}/clear`

**Response:**
```json
{
  "message": "Cart cleared successfully"
}
```

---

### 6. Get Cart Total
**GET** `/cart/{userId}/total`

**Response:**
```json
{
  "total": 2500.00
}
```

---

## 📦 ORDER ENDPOINTS

### 1. Get All Orders (Admin)
**GET** `/orders`

**Response:** Array of all orders

---

### 2. Get Order by ID
**GET** `/orders/{orderId}`

**Response:**
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
  "card": "****1234",
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
  "createdAt": "2025-12-08T00:00:00",
  "updatedAt": "2025-12-08T00:00:00"
}
```

---

### 3. Get User's Orders
**GET** `/orders/user/{userId}`

**Description:** Get all orders for a specific user, sorted by newest first

**Response:** Array of orders

---

### 4. Checkout (Create Order from Cart)
**POST** `/orders/checkout/{userId}`

**Request Body:**
```json
{
  "fullName": "Juan Dela Cruz",
  "address": "123 Main St",
  "city": "Manila",
  "postal": "1000",
  "paymentMethod": "card",
  "card": "1234567890123456"
}
```

**Note:** 
- `paymentMethod` can be "card" or "cod"
- `card` field is optional (only needed for card payments)
- This endpoint will:
  1. Create an order from cart items
  2. Clear the cart
  3. Return the created order

**Response:** Complete order object with items

---

### 5. Update Order Status
**PUT** `/orders/{orderId}/status`

**Request Body:**
```json
{
  "status": "processing"
}
```

**Status Values:**
- `pending` - Order placed
- `processing` - Being prepared
- `shipped` - On the way
- `delivered` - Completed
- `cancelled` - Cancelled

**Response:** Updated order

---

### 6. Cancel Order
**PUT** `/orders/{orderId}/cancel`

**Description:** Set order status to "cancelled"

**Response:** Updated order

---

### 7. Delete Order
**DELETE** `/orders/{orderId}`

**Response:**
```json
{
  "message": "Order deleted successfully"
}
```

---

### 8. Get Orders by Status
**GET** `/orders/status/{status}`

**Example:** `/orders/status/pending`

**Response:** Array of orders with specified status

---

## 📝 Frontend Integration Examples

### React/JavaScript Example - Add to Cart

```javascript
const addToCart = async (userId, productId, quantity) => {
  try {
    const response = await fetch(`http://localhost:8080/api/cart/${userId}/items`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        productId: productId,
        quantity: quantity
      })
    });
    
    const cart = await response.json();
    console.log('Cart updated:', cart);
    return cart;
  } catch (error) {
    console.error('Error adding to cart:', error);
  }
};
```

### React/JavaScript Example - Checkout

```javascript
const checkout = async (userId, formData) => {
  try {
    const response = await fetch(`http://localhost:8080/api/orders/checkout/${userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        fullName: formData.fullName,
        address: formData.address,
        city: formData.city,
        postal: formData.postal,
        paymentMethod: formData.paymentMethod,
        card: formData.card
      })
    });
    
    const order = await response.json();
    console.log('Order created:', order);
    return order;
  } catch (error) {
    console.error('Error creating order:', error);
  }
};
```

### React/JavaScript Example - Get Cart

```javascript
const getCart = async (userId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/cart/${userId}`);
    const cart = await response.json();
    return cart;
  } catch (error) {
    console.error('Error fetching cart:', error);
  }
};
```

### React/JavaScript Example - Update Cart Item

```javascript
const updateCartItem = async (userId, itemId, newQuantity) => {
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
    
    const cart = await response.json();
    return cart;
  } catch (error) {
    console.error('Error updating cart item:', error);
  }
};
```

---

## 🔄 Typical User Flow

1. **Browse Products** → Get products from `/api/products`
2. **Add to Cart** → POST to `/api/cart/{userId}/items`
3. **View Cart** → GET `/api/cart/{userId}`
4. **Update Quantities** → PUT `/api/cart/{userId}/items/{itemId}`
5. **Checkout** → POST `/api/orders/checkout/{userId}`
6. **View Orders** → GET `/api/orders/user/{userId}`
7. **Track Order** → GET `/api/orders/{orderId}`

---

## ⚠️ Error Handling

All endpoints may return error responses:

```json
{
  "timestamp": "2025-12-08T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cart not found",
  "path": "/api/cart/1"
}
```

Common HTTP Status Codes:
- `200` - Success
- `404` - Resource not found
- `500` - Server error

---

## 🎯 Important Notes

1. **User Authentication:** Currently using `userId` in URLs. Implement proper authentication in production.
2. **CORS:** Backend is configured to accept requests from `http://localhost:3000`
3. **Auto-Creation:** Cart is automatically created if it doesn't exist when fetching
4. **Cart Clearing:** Cart is automatically cleared after successful checkout
5. **Timestamps:** All dates are in ISO-8601 format
6. **Currency:** All prices are in Philippine Peso (₱)

---

## 🚀 Testing with Postman/Thunder Client

### Example: Add Item to Cart
```
POST http://localhost:8080/api/cart/1/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

### Example: Checkout
```
POST http://localhost:8080/api/orders/checkout/1
Content-Type: application/json

{
  "fullName": "Juan Dela Cruz",
  "address": "123 Main St",
  "city": "Manila",
  "postal": "1000",
  "paymentMethod": "cod"
}
```
