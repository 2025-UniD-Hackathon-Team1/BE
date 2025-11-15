package practice.deploy.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.deploy.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;
}
