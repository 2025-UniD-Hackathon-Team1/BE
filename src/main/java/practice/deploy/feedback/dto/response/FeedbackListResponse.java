package practice.deploy.feedback.dto.response;

import java.util.List;

public record FeedbackListResponse(
        List<FeedbackItemResponse> feedbackItemResponseList
) {
    public static FeedbackListResponse from(List<FeedbackItemResponse> feedbackItemResponseList) {
        return new FeedbackListResponse(feedbackItemResponseList);
    }
}

