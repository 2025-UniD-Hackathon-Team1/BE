package practice.deploy.feedback.dto.response;

import practice.deploy.feedback.domain.Feedback;

import java.time.LocalDate;
import java.time.LocalTime;

public record FeedbackItemResponse(
        Long feedbackId,
        LocalDate sleepDate,
        LocalTime sleepTime,
        Long heartRate
) {
    public static FeedbackItemResponse from(Feedback feedback) {
        return new FeedbackItemResponse(
                feedback.getId(),
                feedback.getSleepDate(),
                feedback.getSleepTime(),
                feedback.getHeartRate()
        );
    }
}

