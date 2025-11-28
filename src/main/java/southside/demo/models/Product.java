package southside.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String subtitle;
    private Double price;
    private String badge;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    private Integer quantity;
    // ======================
    // GETTERS
    // ======================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Double getPrice() {
        return price;
    }

    public String getBadge() {
        return badge;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    // ======================
    // SETTERS
    // ======================

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
