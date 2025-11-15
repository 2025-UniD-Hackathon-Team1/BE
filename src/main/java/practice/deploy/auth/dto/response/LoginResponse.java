package practice.deploy.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(String username, String accessToken) {

    public static LoginResponse from(String username, String accessToken) {
        return LoginResponse.builder().username(username).accessToken(accessToken).build();
    }
}