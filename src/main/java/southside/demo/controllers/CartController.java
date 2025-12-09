package southside.demo.controllers;

import southside.demo.models.*;
import southside.demo.repository.*;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Transactional
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;

    public CartController(CartRepository cartRepo, CartItemRepository cartItemRepo,
            ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
    }

    /*
     * ============================
     * GET CART BY USER ID
     * ============================
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepo.save(newCart);
                });
        return ResponseEntity.ok(cart);
    }

    /*
     * ============================
     * ADD ITEM TO CART
     * ============================
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        // Get or create cart
        Cart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepo.save(newCart);
                });

        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        // Get product
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepo.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepo.save(existingItem);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice());
            cartItemRepo.save(newItem);
        }

        // Update cart total
        cart.setUpdatedAt(LocalDateTime.now());
        cart.calculateTotal();
        cartRepo.save(cart);

        // Reload cart with items
        cart = cartRepo.findById(cart.getId()).orElseThrow();
        return ResponseEntity.ok(cart);
    }

    /*
     * ============================
     * UPDATE CART ITEM QUANTITY
     * ============================
     */
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> request) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Integer quantity = request.get("quantity");
        if (quantity <= 0) {
            cartItemRepo.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepo.save(item);
        }

        // Update cart total
        cart.setUpdatedAt(LocalDateTime.now());
        cart.calculateTotal();
        cartRepo.save(cart);

        // Reload cart
        cart = cartRepo.findById(cart.getId()).orElseThrow();
        return ResponseEntity.ok(cart);
    }

    /*
     * ============================
     * REMOVE ITEM FROM CART
     * ============================
     */
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long userId,
            @PathVariable Long itemId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to this user's cart");
        }

        // Delete the item
        cartItemRepo.deleteById(itemId); // Use deleteById instead
        cartItemRepo.flush();

        // Clear persistence context to avoid stale data
        cart.getItems().remove(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cart.calculateTotal();
        cartRepo.saveAndFlush(cart);

        // Reload with fresh data
        Cart updatedCart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return ResponseEntity.ok(updatedCart);
    }

    /*
     * ============================
     * REMOVE ITEM BY PRODUCT ID (Alternative endpoint)
     * ============================
     */
    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<Cart> removeItemByProductId(@PathVariable Long userId,
            @PathVariable Long productId) {
        // Get cart
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        // Find item by product ID
        CartItem item = cartItemRepo.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        // Delete the item
        cartItemRepo.delete(item);
        cartItemRepo.flush();

        // Update cart
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        // Reload cart with fresh data
        Cart updatedCart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return ResponseEntity.ok(updatedCart);
    }

    /*
     * ============================
     * CLEAR CART
     * ============================
     */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepo.deleteAll(cart.getItems());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.calculateTotal();
        cartRepo.save(cart);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }

    /*
     * ============================
     * GET CART TOTAL
     * ============================
     */
    @GetMapping("/{userId}/total")
    public ResponseEntity<Map<String, Double>> getCartTotal(@PathVariable Long userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Map<String, Double> response = new HashMap<>();
        response.put("total", cart.getTotalPrice());
        return ResponseEntity.ok(response);
    }
}
