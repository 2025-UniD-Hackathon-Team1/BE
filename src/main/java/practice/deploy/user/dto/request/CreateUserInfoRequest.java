package practice.deploy.user.dto.request;

import practice.deploy.user.domain.type.Experience;
import practice.deploy.user.domain.type.Gender;
import practice.deploy.user.domain.type.ImportantPeriod;

public record CreateUserInfoRequest(
        Gender gender,
        Long age,
        String sleepTime,
        String wakeupTime,
        ImportantPeriod importantPeriod,
        Experience experience,
        Long sensitivityLevel
) { }
