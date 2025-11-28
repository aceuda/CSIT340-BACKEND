package southside.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import southside.demo.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}