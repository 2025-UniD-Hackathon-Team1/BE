package practice.deploy.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findUserById(Long id);
    boolean existsByUsername(String username);
}
