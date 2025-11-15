package practice.deploy.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.coffee.domain.Coffee;
import practice.deploy.user.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    List<Coffee> findByUserAndDrinkDateBetweenOrderByDrinkDateAscDrinkTimeAsc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
    List<Coffee> findAllByUserIdAndDrinkDate(Long userId, LocalDate drinkDate);
}
