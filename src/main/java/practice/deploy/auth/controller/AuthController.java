package practice.deploy.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.deploy.auth.dto.request.LoginRequest;
import practice.deploy.auth.dto.response.LoginResponse;
import practice.deploy.auth.service.AuthService;
import practice.deploy.global.response.ApiResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 및 회원 관련 API")
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "기본 회원가입", description = "아이디와 비밀번호로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signUp(@Valid @RequestBody LoginRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "기본 로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(loginResponse));
    }
}
