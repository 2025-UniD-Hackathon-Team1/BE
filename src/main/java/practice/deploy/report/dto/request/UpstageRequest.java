package practice.deploy.report.dto.request;

import java.util.List;

public record UpstageRequest(String model,
                             List<UpstageMessage> messages,
                             Integer max_tokens) {
}
