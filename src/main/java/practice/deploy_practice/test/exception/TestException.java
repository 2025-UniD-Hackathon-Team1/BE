package practice.deploy_practice.test.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.deploy_practice.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class TestException extends RuntimeException {
    private final ErrorCode errorCode;
}

