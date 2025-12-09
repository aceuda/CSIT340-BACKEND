package southside.demo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String orderSummary;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private Double total;

    @Column(length = 50, nullable = false)
    private String status = "pending"; // pending, processing, shipped, delivered, cancelled

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional shipping fields for frontend
    private String fullName;
    private String address;
    private String city;
    private String postal;
    private String card;

    // ======================
    // JPA LIFECYCLE CALLBACKS
    // ======================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ======================
    // GETTERS
    // ======================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOrderSummary() {
        return orderSummary;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostal() {
        return postal;
    }

    public String getCard() {
        return card;
    }

    // ======================
    // SETTERS
    // ======================

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOrderSummary(String orderSummary) {
        this.orderSummary = orderSummary;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
