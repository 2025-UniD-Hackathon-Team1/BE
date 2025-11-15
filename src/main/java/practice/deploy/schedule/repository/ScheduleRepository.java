package practice.deploy.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.coffee.domain.Coffee;
import practice.deploy.schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByUserIdAndDate(Long userId, LocalDate date);
}
