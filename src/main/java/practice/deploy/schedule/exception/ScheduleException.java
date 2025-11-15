package practice.deploy.schedule.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.deploy.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class ScheduleException extends RuntimeException {
  private final ErrorCode errorCode;
}
