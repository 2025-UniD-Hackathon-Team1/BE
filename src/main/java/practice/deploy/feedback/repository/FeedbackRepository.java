package practice.deploy.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.deploy.feedback.domain.Feedback;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findAllByUserIdAndSleepDate(Long userId, LocalDate sleepDate);
}

