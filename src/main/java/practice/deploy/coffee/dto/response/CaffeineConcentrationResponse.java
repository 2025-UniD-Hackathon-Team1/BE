package practice.deploy.coffee.dto.response;

import java.util.List;

public record CaffeineConcentrationResponse(
        Double currentCaffeine,
        List<CaffeineGraphPoint> graph
) {
    public record CaffeineGraphPoint(
            String time,
            Double caffeine
    ) {}
}

