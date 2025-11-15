package practice.deploy.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.coffee.domain.Coffee;

import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    List<Coffee> findByUserId(Long userId);
}
