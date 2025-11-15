package practice.deploy.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.deploy.global.entity.BaseEntity;
import practice.deploy.user.domain.type.Experience;
import practice.deploy.user.domain.type.Gender;
import practice.deploy.user.domain.type.ImportantPeriod;
import practice.deploy.user.domain.type.Role;

import java.time.LocalTime;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "age")
    private Long age;

    @Column(name = "role")
    private Role role;

    @Column(name = "sleep_time")
    private LocalTime sleepTime;

    @Column(name = "wakeup_time")
    private LocalTime wakeupTime;

    @Column(name = "important_period")
    @Enumerated(EnumType.STRING)
    private ImportantPeriod importantPeriod;

    @Column(name = "experience")
    @Enumerated(EnumType.STRING)
    private Experience experience;

    @Column(name = "sensitivity_level")
    private Long sensitivityLevel;

    @Builder(builderMethodName = "basicLoginBuilder", buildMethodName = "buildBasicLogin")
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.ROLE_USER;
    }

    public void insertUserInfo(Gender gender, Long age, LocalTime sleepTime, LocalTime wakeupTime, ImportantPeriod importantPeriod, Experience experience, Long sensitivityLevel) {
        this.gender = gender;
        this.age = age;
        this.sleepTime = sleepTime;
        this.wakeupTime = wakeupTime;
        this.importantPeriod = importantPeriod;
        this.experience = experience;
        this.sensitivityLevel = sensitivityLevel;
    }
}
