package practice.deploy.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.deploy.report.dto.DailyCaffeineLog;
import practice.deploy.report.dto.response.ReportResponse;
import practice.deploy.user.domain.User;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final DataPreparationService dataPreparationService;
    private final UpstageApiService upstageApiService;

    public ReportResponse generateReport(Long userId, LocalDate endDate) {
        User user = findUserOrThrow(userId);
        LocalDate startDate = endDate.minusDays(30);
        List<DailyCaffeineLog> analysisLogs = dataPreparationService.prepareMonthlyData(user, startDate, endDate);

        if (analysisLogs.isEmpty()) {
            throw new IllegalArgumentException("분석에 필요한 충분한 데이터가 존재하지 않습니다.");
        }

        String finalPrompt = createPrompt(analysisLogs);
        String reportText = upstageApiService.getMinSleepTimeJson(finalPrompt);
        return new ReportResponse(reportText);
    }

    // 프롬프트 생성 로직
    private String createPrompt(List<DailyCaffeineLog> analysisLogs) {
        String dataHeader = "날짜,총 카페인(mg),마지막 섭취 시간,취침 시간,섭취-취침 간격(시간),심장 두근거림(Y/N)\n";

        String dataRows = analysisLogs.stream()
                .map(log -> String.format("%s,%d,%s,%s,%.1f,%s",
                        log.date(), log.totalCaffeine(), log.lastDrinkTime(), log.sleepTime(), log.intervalHours(),
                        log.hasPalpitation() ? "Y" : "N"))
                .collect(Collectors.joining("\n"));

        // 최종 프롬프트 (데이터 + 명확한 분석 지시)
        String prompt = "당신은 수면 과학 전문가입니다. 다음 데이터를 분석하여 **수면을 방해하지 않는 최소 안전 섭취 간격**을 소수점 한 자리(X.X시간)로 계산하세요.\n"
                + "심장 두근거림(Y)이 발생하지 않은 날들 중, 총 섭취량이 높았던 사례를 기준으로 보수적인 간격을 선택해야 합니다.\n\n"
                + "--- 데이터 ---\n"
                + dataHeader
                + dataRows
                + "\n\n"
                + "--- 최종 출력 규칙 ---\n"
                + "1. 출력은 **오직 JSON 객체 하나**여야 합니다. 서문, 설명, 마크다운(```json) 등 다른 텍스트는 절대 포함하지 마세요.\n"
                + "2. JSON 스키마는 다음과 같습니다:\n"
                + "{\"leastSleepTime\" : \"X.X\"}"; // X.X는 계산된 시간 값입니다.

        return prompt;
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserException(USER_NOT_FOUND));
    }
}
