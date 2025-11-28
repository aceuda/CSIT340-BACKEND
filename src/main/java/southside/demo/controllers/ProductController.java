package southside.demo.controllers;

import southside.demo.models.Product;
import southside.demo.repository.ProductRepository;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    /*
     * ============================
     * GET ALL PRODUCTS
     * ============================
     */
    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }

    /*
     * ============================
     * GET PRODUCT BY ID
     * ============================
     */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    /*
     * ============================
     * CREATE PRODUCT
     * ============================
     */
    @PostMapping
    public Product createProduct(@RequestBody Product p) {
        return repo.save(p);
    }

    /*
     * ============================
     * UPDATE PRODUCT
     * ============================
     */
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product p) {
        Product existing = repo.findById(id).orElse(null);

        if (existing == null) {
            return null; // or throw exception
        }

        existing.setName(p.getName());
        existing.setCategory(p.getCategory());
        existing.setSubtitle(p.getSubtitle());
        existing.setPrice(p.getPrice());
        existing.setBadge(p.getBadge());
        existing.setDescription(p.getDescription());
        existing.setImage(p.getImage());
        existing.setQuantity(p.getQuantity());

        return repo.save(existing);
    }

    /*
     * ============================
     * DELETE PRODUCT
     * ============================
     */
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        repo.deleteById(id);
        return "Product deleted successfully";
    }
}
