package practice.deploy.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.deploy.coffee.domain.Coffee;
import practice.deploy.coffee.repository.CoffeeRepository;
import practice.deploy.feedback.domain.Feedback;
import practice.deploy.feedback.repository.FeedbackRepository;
import practice.deploy.report.dto.DailyCaffeineLog;
import practice.deploy.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataPreparationService {

    private final CoffeeRepository coffeeRepository;
    private final FeedbackRepository feedbackRepository;
    // 심장 박동수 척도 (1-5) 중 '두근거림이 있음'으로 판단할 임계값 (4 이상일 때 Y로 판단)
    private static final long PALPITATION_THRESHOLD = 4L;


    /**
     * 특정 사용자(User)의 기간별 커피 기록과 수면 피드백을 통합하여 LLM 분석용 로그를 생성합니다.
     * @param user 현재 로그인된 User 엔티티
     * @param startDate 분석 시작일
     * @param endDate 분석 종료일
     * @return 하루 단위 분석 로그 리스트 (List<DailyCaffeineAnalysisLog>)
     */
    public List<DailyCaffeineLog> prepareMonthlyData(User user, LocalDate startDate, LocalDate endDate) {

        // 1. DB에서 기간별 데이터 로드
        List<Coffee> coffeeLogs = coffeeRepository.findByUserAndDrinkDateBetweenOrderByDrinkDateAscDrinkTimeAsc(user, startDate, endDate);
        List<Feedback> feedbackLogs = feedbackRepository.findByUserAndSleepDateBetweenOrderBySleepDateAsc(user, startDate, endDate);

        // 2. 커피 로그를 날짜별로 그룹화
        Map<LocalDate, List<Coffee>> coffeeByDate = coffeeLogs.stream()
                .collect(Collectors.groupingBy(Coffee::getDrinkDate));

        // 3. 피드백 로그를 날짜별로 Map으로 변환 (빠른 조회를 위함)
        Map<LocalDate, Feedback> feedbackByDate = feedbackLogs.stream()
                .collect(Collectors.toMap(Feedback::getSleepDate, f -> f));

        // 4. 피드백 날짜를 기준으로 통합 및 분석 로그 생성
        return feedbackByDate.entrySet().stream()
                // 해당 날짜에 커피 기록이 있는 경우에만 처리 (데이터 무결성 확보)
                .filter(entry -> coffeeByDate.containsKey(entry.getKey()))
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    Feedback feedback = entry.getValue();
                    List<Coffee> dayCoffees = coffeeByDate.get(date);

                    // 해당 날짜의 커피 기록이 비어있을 경우 (위 filter에서 걸러지지만 안전을 위해 체크)
                    if (dayCoffees.isEmpty()) return null;

                    // A. 하루 총 카페인 섭취량
                    long totalCaffeine = dayCoffees.stream().mapToLong(Coffee::getCaffeineAmount).sum();

                    // B. 마지막 섭취 시간 (리스트의 마지막 요소)
                    LocalTime lastDrinkTime = dayCoffees.get(dayCoffees.size() - 1).getDrinkTime();

                    // C. 심장 두근거림 여부 판단 (임계값 4 이상일 경우 true)
                    boolean hasPalpitation = feedback.getHeartRate() >= PALPITATION_THRESHOLD;

                    // D. 섭취-취침 간격 (시간) 계산
                    double intervalHours = calculateInterval(lastDrinkTime, feedback.getSleepTime());

                    // 최종 로그 생성
                    return new DailyCaffeineLog(
                            date, totalCaffeine, lastDrinkTime, feedback.getSleepTime(), intervalHours, hasPalpitation
                    );
                })
                .filter(log -> log != null)
                .collect(Collectors.toList());
    }

    /**
     * 마지막 섭취 시간과 취침 시간 사이의 간격을 시간(소수점)으로 계산합니다.
     * 날짜 경계를 넘는 경우(예: 23시 섭취, 익일 01시 취침)를 고려하여 계산합니다.
     */
    private double calculateInterval(LocalTime lastDrinkTime, LocalTime sleepTime) {
        long minutesBetween = ChronoUnit.MINUTES.between(lastDrinkTime, sleepTime);

        // 취침 시간이 섭취 시간보다 논리적으로 앞설 경우, 24시간(1440분)을 더해 익일로 간주
        if (minutesBetween < 0) {
            minutesBetween += 24 * 60;
        }

        return (double) minutesBetween / 60.0;
    }
}