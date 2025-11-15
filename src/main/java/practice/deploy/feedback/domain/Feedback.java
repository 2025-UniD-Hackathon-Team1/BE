package practice.deploy.feedback.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.deploy.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "feedback")
@Entity
@Getter
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id2", nullable = false)
    private User user;

    @Column(name = "sleep_date")
    private LocalDate sleepDate;

    @Column(name = "sleep_time")
    private LocalTime sleepTime;

    @Column(name = "heart_rate")
    private Long heartRate;

    @Builder
    public Feedback(User user, LocalDate sleepDate, LocalTime sleepTime, Long heartRate) {
        this.user = user;
        this.sleepDate = sleepDate;
        this.sleepTime = sleepTime;
        this.heartRate = heartRate;
    }
}

