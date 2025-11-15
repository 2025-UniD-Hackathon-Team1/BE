
package practice.deploy.coffee.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import practice.deploy.user.domain.User;

@Table(name = "coffee")
@Entity
@Getter
@NoArgsConstructor
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "drink_time", nullable = false)
    private LocalTime drinkTime;

    @Column(nullable = false)
    private String name;

    @Column(name = "caffeine_amount", nullable = false)
    private Long caffeineAmount;

    @Builder
    public Coffee(User user, LocalTime drinkTime, String name, Long caffeineAmount) {
        this.user = user; // 이제 Long이 아니라 User 객체를 직접 받습니다.
        this.drinkTime = drinkTime;
        this.name = name;
        this.caffeineAmount = caffeineAmount;
    }

}
