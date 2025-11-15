package practice.deploy.report.dto.upstage;

import java.util.List;

public record UpstageResponse(
        List<UpstageChoice> choices
) {
}
