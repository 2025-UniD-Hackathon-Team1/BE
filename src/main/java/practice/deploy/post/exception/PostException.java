package practice.deploy.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.deploy.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {
    private final ErrorCode errorCode;
}