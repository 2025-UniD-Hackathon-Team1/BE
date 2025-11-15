package practice.deploy.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import practice.deploy.global.response.ApiResponse;
import practice.deploy.schedule.dto.request.ScheduleCreateRequest;
import practice.deploy.schedule.dto.response.ScheduleListResponse;
import practice.deploy.schedule.entity.Schedule;
import practice.deploy.schedule.service.ScheduleService;
import practice.deploy.user.utils.CustomUserDetails;

@Tag(name = "Schedule", description = "일정 관련 API")
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 생성 API", description = "로그인 후에 진행합니다. 시간은 11:30 형식으로 입력해주세요.")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestBody ScheduleCreateRequest request) {
        scheduleService.createSchedule(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
    }

    @Operation(summary = "하루 일정 조회 API", description = "로그인 후에 진행합니다. 오늘 하루 일정을 조회합니다. 2025-11-15와 같이 날짜를 입력해주세요.")
    @GetMapping()
    public ResponseEntity<ApiResponse<Object>> getUserSchedules(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @RequestParam LocalDate date) {
        ScheduleListResponse response = scheduleService.getScheduleList(userDetails.getId(), date);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
    }
}
