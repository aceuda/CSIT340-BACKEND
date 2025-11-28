package southside.demo.controllers;

import org.springframework.web.bind.annotation.*;
import southside.demo.models.User;
import southside.demo.repository.UserRepository;

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
    public String signup(@RequestBody User user) {
        if (repo.findByEmail(user.getEmail()) != null) {
            return "Email already exists";
        }

        repo.save(user);
        return "Signup successful";
    }

    // LOGIN
    @PostMapping("/login")
    public Object login(@RequestBody User user) {

        User existing = repo.findByEmail(user.getEmail());
        if (existing == null) {
            return "User not found";
        }

        if (!existing.getPassword().equals(user.getPassword())) {
            return "Invalid password";
        }

        return existing; // Return user data
    }
}
