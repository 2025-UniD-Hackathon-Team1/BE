package practice.deploy.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import practice.deploy.coffee.dto.request.CoffeeRequest;
import practice.deploy.coffee.dto.response.AlertnessResponse;
import practice.deploy.coffee.dto.response.CoffeeListResponse;
import practice.deploy.coffee.dto.response.CaffeineConcentrationResponse;
import practice.deploy.coffee.service.CoffeeService;
import practice.deploy.global.response.ApiResponse;
import practice.deploy.user.utils.CustomUserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Tag(name="Coffee", description = "커피 관련 API")
@RequestMapping("/api/v1/coffee")
public class CoffeeController {

    private final CoffeeService coffeeService;

    @Operation(summary = "하루 커피 등록 API", description = "로그인 후에 진행해주세요. 시간은 11:30 와 같이 String으로 입력해주세요.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> registerCoffee(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CoffeeRequest request) {
        coffeeService.addCoffee(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "하루 커피 조회 API", description = "로그인 후에 진행해주세요. '2025-11-14' 와 같이 날짜를 입력하여 오늘 마신 커피를 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<Object>> getCoffeeList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestParam(value="date") LocalDate drinkDate) {
        CoffeeListResponse response = coffeeService.getCoffeeList(drinkDate, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
    }

    @Operation(summary = "카페인 농도 조회 API", description = "로그인 후에 진행해주세요. 현재 시간을 입력하여 현재 카페인 농도와 시간대별 카페인 농도를 조회합니다.")
    @GetMapping("/caffeine")
    public ResponseEntity<ApiResponse<Object>> getCaffeineConcentration(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "currentTime") LocalDateTime currentTime) {
        CaffeineConcentrationResponse response = coffeeService.getCaffeineConcentration(userDetails.getId(), currentTime);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
    }

    @Operation(summary = "각성도 조회 API", description = "로그인 후에 진행해주세요. 현재 시간을 입력하여 현재 각성도와 각성 종료 시간을 조회합니다.")
    @GetMapping("/alertness")
    public ResponseEntity<ApiResponse<Object>> getAlertness(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "currentTime") LocalDateTime currentTime) {
        AlertnessResponse response = coffeeService.getAlertness(userDetails.getId(), currentTime);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
    }
}