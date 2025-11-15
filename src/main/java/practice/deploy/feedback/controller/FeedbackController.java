package practice.deploy.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import practice.deploy.feedback.dto.request.FeedbackRequest;
import practice.deploy.feedback.dto.response.FeedbackListResponse;
import practice.deploy.feedback.service.FeedbackService;
import practice.deploy.global.response.ApiResponse;
import practice.deploy.user.utils.CustomUserDetails;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "피드백 관련 API")
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 등록 API", description = "로그인 후에 진행해주세요. 시간은 11:30 와 같이 String으로 입력해주세요.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> registerFeedback(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FeedbackRequest request) {
        feedbackService.addFeedback(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }
}

