# 🔐 User Role Implementation Guide

## ✅ **BACKEND CHANGES COMPLETED**

I've updated your backend to support user roles (admin vs regular user).

---

## 📊 **DATABASE CHANGES NEEDED**

### **Option 1: Auto-Update (Easiest)**
Your `application.properties` already has:
```properties
spring.jpa.hibernate.ddl-auto=update
```
Just restart your server and Spring will automatically add the `role` column!

### **Option 2: Manual SQL (If Option 1 doesn't work)**
Run this in MySQL:
```sql
ALTER TABLE users ADD COLUMN role VARCHAR(50) DEFAULT 'user';
UPDATE users SET role = 'user' WHERE role IS NULL;
```

---

## 🎯 **HOW IT WORKS NOW**

### **1. SIGNUP**
**Endpoint:** `POST http://localhost:8080/api/users/signup`

**Request (Regular User):**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "message": "Signup successful",
  "role": "user"
}
```

---

### **2. LOGIN**
**Endpoint:** `POST http://localhost:8080/api/users/login`

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (Regular User):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "user"
}
```

**Response (Admin User):**
```json
{
  "id": 2,
  "name": "Admin",
  "email": "admin@example.com",
  "role": "admin"
}
```

---

### **3. ADMIN SIGNUP (NEW!)**
**Endpoint:** `POST http://localhost:8080/api/users/admin/signup`

**Request:**
```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "message": "Admin account created successfully",
  "role": "admin"
}
```

---

## 💻 **FRONTEND INTEGRATION**

### **1. Login Handler**
```javascript
const handleLogin = async (formData) => {
  try {
    const response = await fetch('http://localhost:8080/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: formData.email,
        password: formData.password
      })
    });
    
    const userData = await response.json();
    
    // Check if login was successful
    if (userData.id) {
      // Save user data to localStorage
      localStorage.setItem('user', JSON.stringify(userData));
      localStorage.setItem('userId', userData.id);
      localStorage.setItem('userRole', userData.role); // Save role
      localStorage.setItem('userName', userData.name);
      
      console.log('Logged in as:', userData.role); // Shows "admin" or "user"
      
      // Redirect based on role
      if (userData.role === 'admin') {
        navigate('/admin/dashboard'); // Admin goes to admin page
      } else {
        navigate('/shop'); // Regular user goes to shop
      }
      
      alert(`Welcome ${userData.name}! (${userData.role})`);
    } else {
      // Login failed
      alert(userData.message || 'Login failed');
    }
  } catch (error) {
    console.error('Login error:', error);
    alert('Network error. Please try again.');
  }
};
```

---

### **2. Signup Handler (with Optional Role)**
```javascript
const handleSignup = async (formData) => {
  try {
    const response = await fetch('http://localhost:8080/api/users/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: formData.name,
        email: formData.email,
        password: formData.password,
        role: formData.role || 'user' // Optional: default to 'user'
      })
    });
    
    const result = await response.json();
    
    if (result.message === 'Signup successful') {
      alert(`Account created successfully as ${result.role}`);
      navigate('/login');
    } else {
      alert(result.message);
    }
  } catch (error) {
    console.error('Signup error:', error);
  }
};
```

---

### **3. Check User Role (Anywhere in your app)**
```javascript
// Get current user's role
const getUserRole = () => {
  return localStorage.getItem('userRole'); // Returns "admin" or "user"
};

// Check if user is admin
const isAdmin = () => {
  return getUserRole() === 'admin';
};

// Example usage
if (isAdmin()) {
  console.log('User is an admin!');
  // Show admin features
} else {
  console.log('User is a regular user');
  // Show regular features
}
```

---

### **4. Protected Routes (React Router Example)**
```javascript
import { Navigate } from 'react-router-dom';

// Admin-only route
const AdminRoute = ({ children }) => {
  const userRole = localStorage.getItem('userRole');
  return userRole === 'admin' ? children : <Navigate to="/login" />;
};

// Usage in your routes
<Route 
  path="/admin/dashboard" 
  element={
    <AdminRoute>
      <AdminDashboard />
    </AdminRoute>
  } 
/>
```

---

### **5. Conditional Rendering Based on Role**
```javascript
const Navbar = () => {
  const userRole = localStorage.getItem('userRole');
  
  return (
    <nav>
      <Link to="/shop">Shop</Link>
      <Link to="/cart">Cart</Link>
      
      {/* Show admin link only for admins */}
      {userRole === 'admin' && (
        <Link to="/admin/dashboard">Admin Panel</Link>
      )}
      
      <Link to="/orders">My Orders</Link>
    </nav>
  );
};
```

---

## 🗄️ **DATABASE VERIFICATION**

After restarting your server, check your database:

```sql
-- View users table structure
DESCRIBE users;

-- Should show:
-- id, name, email, password, role

-- View all users with roles
SELECT id, name, email, role FROM users;
```

---

## 🧪 **TESTING STEPS**

### **1. Create Admin User**
Use Postman or your frontend:
```
POST http://localhost:8080/api/users/admin/signup
Body:
{
  "name": "Admin",
  "email": "admin@southside.com",
  "password": "admin123"
}
```

### **2. Create Regular User**
```
POST http://localhost:8080/api/users/signup
Body:
{
  "name": "John Doe",
  "email": "john@southside.com",
  "password": "user123"
}
```

### **3. Test Login (Admin)**
```
POST http://localhost:8080/api/users/login
Body:
{
  "email": "admin@southside.com",
  "password": "admin123"
}

Response should show: "role": "admin"
```

### **4. Test Login (User)**
```
POST http://localhost:8080/api/users/login
Body:
{
  "email": "john@southside.com",
  "password": "user123"
}

Response should show: "role": "user"
```

---

## 📋 **SUMMARY**

### **What Changed in Backend:**
1. ✅ Added `role` field to User model (default: "user")
2. ✅ Updated signup to save role
3. ✅ Updated login to return role in response
4. ✅ Added admin signup endpoint
5. ✅ Better error handling with structured responses

### **What Shows in Database:**
- When admin logs in → `role = "admin"` is stored and returned
- When user logs in → `role = "user"` is stored and returned

### **What You Need to Do in Frontend:**
1. Store the role from login response
2. Use it to control navigation (redirect to admin/user pages)
3. Show/hide features based on role
4. Protect admin routes

---

## 🚀 **Next Steps**

1. Restart your Spring Boot server (to auto-create the role column)
2. Create an admin account using the admin signup endpoint
3. Update your frontend login to save and use the role
4. Test both admin and user login flows

Your backend is ready! The database will now track whether an admin or regular user is logging in. 🎉
