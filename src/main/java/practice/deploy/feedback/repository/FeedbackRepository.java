package practice.deploy.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.feedback.domain.Feedback;
import practice.deploy.user.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserAndSleepDateBetweenOrderBySleepDateAsc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
    List<Feedback> findAllByUserIdAndSleepDate(Long userId, LocalDate sleepDate);
}

