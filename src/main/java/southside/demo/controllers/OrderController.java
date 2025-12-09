package southside.demo.controllers;

import southside.demo.models.*;
import southside.demo.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;

    public OrderController(OrderRepository orderRepo, OrderItemRepository orderItemRepo,
            CartRepository cartRepo, CartItemRepository cartItemRepo,
            ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
    }

    /*
     * ============================
     * GET ALL ORDERS
     * ============================
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        return ResponseEntity.ok(orders);
    }

    /*
     * ============================
     * GET ORDER BY ID
     * ============================
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }

    /*
     * ============================
     * GET ORDERS BY USER ID
     * ============================
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(orders);
    }

    /*
     * ============================
     * CREATE ORDER FROM CART (CHECKOUT)
     * ============================
     */
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<Order> createOrderFromCart(@PathVariable Long userId,
            @RequestBody Order orderData) {
        // Get user's cart
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create new order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("pending");
        order.setTotal(cart.getTotalPrice());

        // Map frontend fields from orderData
        if (orderData.getFullName() != null) {
            order.setFullName(orderData.getFullName());
        }
        if (orderData.getAddress() != null) {
            order.setAddress(orderData.getAddress());
        }
        if (orderData.getCity() != null) {
            order.setCity(orderData.getCity());
        }
        if (orderData.getPostal() != null) {
            order.setPostal(orderData.getPostal());
        }
        if (orderData.getPaymentMethod() != null) {
            order.setPaymentMethod(orderData.getPaymentMethod());
        } else {
            order.setPaymentMethod("cod"); // Default to Cash on Delivery
        }
        if (orderData.getCard() != null) {
            order.setCard(orderData.getCard());
        }

        // Generate order summary
        StringBuilder summary = new StringBuilder();
        for (CartItem item : cart.getItems()) {
            summary.append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(", ");
        }
        if (summary.length() > 0) {
            summary.setLength(summary.length() - 2); // Remove last comma
        }
        order.setOrderSummary(summary.toString());

        // Save order
        Order savedOrder = orderRepo.save(order);

        // Transfer cart items to order items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepo.save(orderItem);
        }

        // Clear the cart
        cartItemRepo.deleteAll(cart.getItems());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.calculateTotal();
        cartRepo.save(cart);

        // Reload order with items
        savedOrder = orderRepo.findById(savedOrder.getId()).orElseThrow();
        return ResponseEntity.ok(savedOrder);
    }

    /*
     * ============================
     * UPDATE ORDER STATUS
     * ============================
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String status = request.get("status");
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepo.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    /*
     * ============================
     * CANCEL ORDER
     * ============================
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("cancelled");
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepo.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    /*
     * ============================
     * DELETE ORDER
     * ============================
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long id) {
        orderRepo.deleteById(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");
        return ResponseEntity.ok(response);
    }

    /*
     * ============================
     * GET ORDERS BY STATUS
     * ============================
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepo.findByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
