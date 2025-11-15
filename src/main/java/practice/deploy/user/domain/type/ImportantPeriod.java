package practice.deploy.user.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImportantPeriod {
    MORNING("오전9시-12시"),
    LUNCH("오후1시-3시"),
    EVENING("오후3시-6시");

    private final String label;

    public String getLabel() {
        return label;
    }
}
