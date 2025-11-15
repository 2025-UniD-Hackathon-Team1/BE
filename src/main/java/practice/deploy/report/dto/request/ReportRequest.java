package practice.deploy.report.dto.request;

import java.time.LocalDate;

public record ReportRequest(
        LocalDate endDate
) {
}
