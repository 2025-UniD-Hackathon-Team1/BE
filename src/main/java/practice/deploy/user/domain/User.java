package practice.deploy.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.deploy.coffee.domain.Coffee;
import practice.deploy.global.entity.BaseEntity;
import practice.deploy.user.domain.type.Gender;
import practice.deploy.user.domain.type.Role;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coffee> coffeeList = new ArrayList<>();

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

    @Builder(builderMethodName = "basicLoginBuilder", buildMethodName = "buildBasicLogin")
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.ROLE_USER;
    }
}
