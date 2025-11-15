package practice.deploy.report.dto.response;

import java.util.List;

public record UpstageResponse(
        List<UpstageChoice> choices
) {
}
