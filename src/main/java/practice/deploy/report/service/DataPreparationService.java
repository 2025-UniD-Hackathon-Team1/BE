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
    private static final long PALPITATION_THRESHOLD = 4L;

    /**
     * 특정 사용자(User)의 기간별 커피 기록과 수면 피드백을 통합하여 LLM 분석용 로그를 생성합니다.
     * @param user 현재 로그인된 User 엔티티
     * @param startDate 분석 시작일
     * @param endDate 분석 종료일
     * @return 하루 단위 분석 로그 리스트 (List<DailyCaffeineAnalysisLog>)
     */
    public List<DailyCaffeineLog> prepareMonthlyData(User user, LocalDate startDate, LocalDate endDate) {

        List<Coffee> coffeeLogs = coffeeRepository.findByUserAndDrinkDateBetweenOrderByDrinkDateAscDrinkTimeAsc(user, startDate, endDate);
        List<Feedback> feedbackLogs = feedbackRepository.findByUserAndSleepDateBetweenOrderBySleepDateAsc(user, startDate, endDate);

        Map<LocalDate, List<Coffee>> coffeeByDate = coffeeLogs.stream()
                .collect(Collectors.groupingBy(Coffee::getDrinkDate));

        Map<LocalDate, Feedback> feedbackByDate = feedbackLogs.stream()
                .collect(Collectors.toMap(Feedback::getSleepDate, f -> f));

        return feedbackByDate.entrySet().stream()
                .filter(entry -> coffeeByDate.containsKey(entry.getKey()))
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    Feedback feedback = entry.getValue();
                    List<Coffee> dayCoffees = coffeeByDate.get(date);

                    if (dayCoffees.isEmpty()) return null;

                    long totalCaffeine = dayCoffees.stream().mapToLong(Coffee::getCaffeineAmount).sum();

                    LocalTime lastDrinkTime = dayCoffees.get(dayCoffees.size() - 1).getDrinkTime();

                    boolean hasPalpitation = feedback.getHeartRate() >= PALPITATION_THRESHOLD;

                    double intervalHours = calculateInterval(lastDrinkTime, feedback.getSleepTime());

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