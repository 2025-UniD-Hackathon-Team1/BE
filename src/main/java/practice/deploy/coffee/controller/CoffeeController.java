package practice.deploy.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import practice.deploy.coffee.dto.request.CoffeeRequest;
import practice.deploy.coffee.service.CoffeeService;
import practice.deploy.global.response.ApiResponse;
import practice.deploy.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name="Coffee", description = "커피 관련 API")
@RequestMapping("/api/v1/coffee")
public class CoffeeController {

    private final CoffeeService coffeeService;

    @Operation(summary = "하루 커피 등록 API", description = "로그인 후에 진행해주세요. 시간은 11:30 와 같이 입력해주세요. Strig입니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> registerCoffee(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CoffeeRequest request) {
        coffeeService.addCoffee(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }
//
//    @Operation(summary = "하루 커피 조회 API", description = "로그인 후에 진행해주세요. 오늘 마신 커피를 조회합니다.")
//    @GetMapping("/today")
//    public CoffeeResponse getCoffeeById(@PathVariable Long id) {
//        return coffeeService.getById(id);
//    }
}