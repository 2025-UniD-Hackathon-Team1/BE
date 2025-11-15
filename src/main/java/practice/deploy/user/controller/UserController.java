package practice.deploy.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.deploy.global.response.ApiResponse;
import practice.deploy.user.dto.request.CreateUserInfoRequest;
import practice.deploy.user.service.UserService;
import practice.deploy.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "유저 관련 API")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 관련 정보 입력 API", description = "로그인 후에 진행해주세요. 성별에는 `MALE` 또는 `FEMALE` 을 입력해주세요." +
            "sleepTime과 wakeupTime은 11:30, 07:30 와 같이 String으로 보내주세요. importantPeriod은 `MORNING`, `LUNCH`, `EVENING` 으로 입력해주세요." +
            "experience는 `FREQUENTLY` ,`SOMETIMES` , `RARELY` ,`NEVER` 중 하나를 선택해서 보내주세요")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CreateUserInfoRequest request) {
        userService.createUserInfo(userDetails.getId(),request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }
}
