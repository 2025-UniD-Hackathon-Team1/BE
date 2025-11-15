package practice.deploy.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.deploy.report.dto.request.ReportRequest;
import practice.deploy.report.dto.response.ReportResponse;
import practice.deploy.report.service.ReportService;
import practice.deploy.user.utils.CustomUserDetails;

import javax.swing.plaf.SeparatorUI;

@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "Report", description = "리포트 관련 API")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "리포트 제공 API")
    @PostMapping
    public ResponseEntity<ReportResponse> getReport(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody ReportRequest request) {

        try {
            ReportResponse result = reportService.generateReport(userDetails.getId(), request.endDate());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ReportResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ReportResponse("분석 중 오류 발생: " + e.getMessage()));
        }
    }
}
