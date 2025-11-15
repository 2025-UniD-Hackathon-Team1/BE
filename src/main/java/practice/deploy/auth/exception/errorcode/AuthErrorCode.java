package practice.deploy.auth.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import practice.deploy.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.")
    ,
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
