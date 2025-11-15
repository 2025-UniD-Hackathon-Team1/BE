package practice.deploy.user.domain.type;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Experience {
    FREQUENTLY("자주 있다"),
    SOMETIMES("가끔 있다"),
    RARELY("거의 없다"),
    NEVER("없음");

    private final String label;

    public String getLabel() {
        return label;
    }
}
