package practice.deploy.user.dto.request;

import practice.deploy.user.domain.type.Gender;

public record CreateUserInfoRequest(
        Gender gender,
        Long age,
        String sleepTime
) { }
