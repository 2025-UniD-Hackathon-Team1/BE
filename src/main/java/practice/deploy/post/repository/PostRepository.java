package practice.deploy.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.post.domain.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
}
