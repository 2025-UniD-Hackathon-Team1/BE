package practice.deploy.feedback.dto.request;

import java.time.LocalDate;

public record FeedbackRequest(
        LocalDate sleepDate,
        String sleepTime,
        Long heartRate
) {}

