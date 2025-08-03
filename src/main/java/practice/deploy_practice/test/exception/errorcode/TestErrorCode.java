package practice.deploy_practice.test.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import practice.deploy_practice.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TestErrorCode implements ErrorCode {
    TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "테스트 코드를 찾을 수 없습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}