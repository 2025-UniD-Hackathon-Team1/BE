package practice.deploy.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import practice.deploy.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Schedule(LocalDate date, LocalTime time, String name, User user) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.user = user;
    }
}
