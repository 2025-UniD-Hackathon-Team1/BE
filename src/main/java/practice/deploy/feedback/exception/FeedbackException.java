package practice.deploy.feedback.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.deploy.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class FeedbackException extends RuntimeException {
    private final ErrorCode errorCode;
}

