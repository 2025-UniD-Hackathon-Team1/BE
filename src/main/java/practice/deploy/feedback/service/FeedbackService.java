package practice.deploy.feedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.deploy.feedback.domain.Feedback;
import practice.deploy.feedback.dto.request.FeedbackRequest;
import practice.deploy.feedback.dto.response.FeedbackItemResponse;
import practice.deploy.feedback.dto.response.FeedbackListResponse;
import practice.deploy.feedback.repository.FeedbackRepository;
import practice.deploy.user.domain.User;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addFeedback(Long userId, FeedbackRequest request) {
        User user = findUserOrThrow(userId);
        LocalTime sleepTime = request.sleepTime() != null ? LocalTime.parse(request.sleepTime()) : null;

        Feedback feedback = Feedback.builder()
                .user(user)
                .sleepDate(request.sleepDate())
                .sleepTime(sleepTime)
                .heartRate(request.heartRate())
                .build();
        feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public FeedbackListResponse getFeedbackList(LocalDate sleepDate, Long userId) {
        findUserOrThrow(userId);

        List<Feedback> feedbackList = feedbackRepository.findAllByUserIdAndSleepDate(userId, sleepDate);
        List<FeedbackItemResponse> feedbackResponseList = feedbackList.stream()
                .map(FeedbackItemResponse::from)
                .collect(Collectors.toList());

        return FeedbackListResponse.from(feedbackResponseList);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}

