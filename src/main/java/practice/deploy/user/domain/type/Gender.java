package practice.deploy.user.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String label;

    public String getLabel() {
        return label;
    }
}
