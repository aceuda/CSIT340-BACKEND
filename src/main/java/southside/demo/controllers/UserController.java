package southside.demo.controllers;

import org.springframework.web.bind.annotation.*;
import southside.demo.models.User;
import southside.demo.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // SIGNUP
    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if (repo.findByEmail(user.getEmail()) != null) {
            response.put("message", "Email already exists");
            return response;
        }

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user"); // Default to regular user
        }

        repo.save(user);
        response.put("message", "Signup successful");
        response.put("role", user.getRole());
        return response;
    }

    // LOGIN
    @PostMapping("/login")
    public Object login(@RequestBody User user) {
        User existing = repo.findByEmail(user.getEmail());

        if (existing == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return error;
        }

        if (!existing.getPassword().equals(user.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid password");
            return error;
        }

        // Return user data including role
        Map<String, Object> response = new HashMap<>();
        response.put("id", existing.getId());
        response.put("name", existing.getName());
        response.put("email", existing.getEmail());
        response.put("role", existing.getRole()); // Include role in response

        return response;
    }

    // ADMIN SIGNUP (Optional endpoint for creating admin accounts)
    @PostMapping("/admin/signup")
    public Map<String, String> adminSignup(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if (repo.findByEmail(user.getEmail()) != null) {
            response.put("message", "Email already exists");
            return response;
        }

        user.setRole("admin"); // Force role to admin
        repo.save(user);

        response.put("message", "Admin account created successfully");
        response.put("role", "admin");
        return response;
    }
}
