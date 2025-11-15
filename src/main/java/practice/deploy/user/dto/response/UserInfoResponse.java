package practice.deploy.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import practice.deploy.user.domain.User;
import practice.deploy.user.domain.type.Gender;

import java.time.LocalDate;
import java.time.LocalTime;

public record UserInfoResponse(
        Gender gender,
        Long age,
        @JsonFormat(pattern = "HH:mm")
        LocalTime sleepTime
) {
    public static UserInfoResponse from(User user){
        return new UserInfoResponse(
                user.getGender(),
                user.getAge(),
                user.getSleepTime()
        );
    }
}
