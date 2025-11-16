package practice.deploy.coffee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import practice.deploy.coffee.domain.Coffee;
import practice.deploy.coffee.dto.request.CoffeeRequest;
import practice.deploy.coffee.dto.response.AlertnessResponse;
import practice.deploy.coffee.dto.response.CaffeineConcentrationResponse;
import practice.deploy.coffee.dto.response.CoffeeItemResponse;
import practice.deploy.coffee.dto.response.CoffeeListResponse;
import practice.deploy.coffee.repository.CoffeeRepository;

import practice.deploy.coffee.util.CaffeineModel;
import practice.deploy.user.domain.User;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addCoffee(Long userId, CoffeeRequest request) {

        User user = findUserOrThrow(userId);
        LocalTime drinkTime = LocalTime.parse(request.drinkTime());

        Coffee coffee = Coffee.builder()
                .name(request.coffeeName())
                .caffeineAmount(request.caffeineAmount())
                .drinkTime(drinkTime)
                .drinkDate(request.drinkDate())
                .user(user)
                .build();

        coffeeRepository.save(coffee);
    }

    @Transactional(readOnly = true)
    public CoffeeListResponse getCoffeeList(LocalDate drinkDate, Long userId){
        findUserOrThrow(userId);

        List<Coffee> coffeeList = coffeeRepository.findAllByUserIdAndDrinkDate(userId, drinkDate);
        List<CoffeeItemResponse> coffeeResponseList = coffeeList.stream()
                .map(CoffeeItemResponse::from)
                .collect(Collectors.toList());

        return CoffeeListResponse.from(coffeeResponseList);
    }

    /* --------------------------- Caffeine Graph Service --------------------------- */
    @Transactional(readOnly = true)
    public CaffeineConcentrationResponse getCaffeineConcentration(Long userId, LocalDateTime currentTime) {

        User user = findUserOrThrow(userId);

        // 오늘 날짜 조회
        LocalDate today = currentTime.toLocalDate();
        List<Coffee> coffeeList = coffeeRepository.findAllByUserIdAndDrinkDate(userId, today);

        if (coffeeList.isEmpty()) {
            return new CaffeineConcentrationResponse(0.0, new ArrayList<>());
        }

        // 첫 음료 시각 계산
        LocalTime firstDrinkTime = coffeeList.stream()
                .map(Coffee::getDrinkTime)
                .min(LocalTime::compareTo)
                .orElse(currentTime.toLocalTime());

        // 사용자 나이 → 반감기 계산
        int age = user.getAge() != null ? user.getAge().intValue() : 30;
        double tHalf = CaffeineModel.estimateTHalf(age, "normal");

        // 시작/끝 시간
        LocalDateTime firstDrinkDateTime = LocalDateTime.of(today, firstDrinkTime);
        LocalDateTime graphStartTime = firstDrinkDateTime;
        // 오늘 밤 24시(자정) 전까지 그래프 생성
        LocalDateTime graphEndTime = today.atTime(23, 59);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // 현재 남은 카페인 총량
        double currentCaffeine = calculateRemainingCaffeine(coffeeList, currentTime, tHalf);

        // 1시간 간격 그래프
        List<CaffeineConcentrationResponse.CaffeineGraphPoint> graph = new ArrayList<>();

        while (!graphStartTime.isAfter(graphEndTime)) {

            double caffeineAtTime = calculateRemainingCaffeine(coffeeList, graphStartTime, tHalf);

            graph.add(new CaffeineConcentrationResponse.CaffeineGraphPoint(
                    graphStartTime.toLocalTime().format(timeFormatter),
                    Math.round(caffeineAtTime * 10.0) / 10.0
            ));

            graphStartTime = graphStartTime.plusHours(1);
        }

        return new CaffeineConcentrationResponse(
                Math.round(currentCaffeine * 10.0) / 10.0,
                graph
        );
    }

    /* --------------------------- Core Logic: Remaining Caffeine --------------------------- */
    private double calculateRemainingCaffeine(List<Coffee> coffeeList, LocalDateTime targetTime, double tHalf) {

        double total = 0.0;

        for (Coffee coffee : coffeeList) {

            LocalDateTime drinkDateTime =
                    LocalDateTime.of(coffee.getDrinkDate(), coffee.getDrinkTime());

            if (drinkDateTime.isAfter(targetTime)) {
                continue;
            }

            double hoursElapsed = calculateHoursBetween(drinkDateTime, targetTime);
            if (hoursElapsed < 0) continue;

            // -------- 핵심: 단순 반감기 기반 섭취량 감소 --------
            // 남은 카페인 = mg × (1/2)^(경과시간 / 반감기)
            double remaining = coffee.getCaffeineAmount() *
                    Math.pow(0.5, hoursElapsed / tHalf);
            // -----------------------------------------------------

            total += remaining;
        }

        return total;
    }

    /* --------------------------- Alertness Service --------------------------- */
    @Transactional(readOnly = true)
    public AlertnessResponse getAlertness(Long userId, LocalDateTime currentTime) {
        User user = findUserOrThrow(userId);
        
        // 오늘 날짜 조회
        LocalDate today = currentTime.toLocalDate();
        List<Coffee> coffeeList = coffeeRepository.findAllByUserIdAndDrinkDate(userId, today);
        
        if (coffeeList.isEmpty()) {
            return new AlertnessResponse(0.1, null); // baseline 각성도
        }
        
        // 사용자 나이
        int age = user.getAge() != null ? user.getAge().intValue() : 30;
        double tHalf = CaffeineModel.estimateTHalf(age, "normal");
        double weight = 70.0; // 기본 체중
        
        // 혈중 농도로 변환 (concentrationWithAbsorption 사용)
        double currentConcentration = 0.0;
        
        // 모든 음료의 혈중 농도를 합산
        for (Coffee coffee : coffeeList) {
            LocalDateTime drinkDateTime = LocalDateTime.of(coffee.getDrinkDate(), coffee.getDrinkTime());
            if (drinkDateTime.isAfter(currentTime)) {
                continue;
            }
            double hoursElapsed = calculateHoursBetween(drinkDateTime, currentTime);
            if (hoursElapsed >= 0) {
                double concentration = CaffeineModel.concentrationWithAbsorption(
                        hoursElapsed,
                        coffee.getCaffeineAmount().doubleValue(),
                        weight,
                        tHalf
                );
                currentConcentration += concentration;
            }
        }
        
        // 내성 계산을 위한 주간 섭취량 계산 (feedback 데이터 활용)
        int weekNumber = calculateWeekNumber(userId, today);
        
        // 현재 각성도 계산
        double currentAlertness = CaffeineModel.caffeineEffect(currentConcentration, age, weekNumber, "alertness");
        
        // 각성 종료 시간 계산 (각성도가 baseline(0.1) 이하로 떨어지는 시점)
        LocalDateTime alertnessEndTime = calculateAlertnessEndTime(
                coffeeList, currentTime, age, weekNumber, tHalf, weight
        );
        
        return new AlertnessResponse(
                Math.round(currentAlertness * 100.0) / 100.0,
                alertnessEndTime
        );
    }
    
    private int calculateWeekNumber(Long userId, LocalDate today) {
        // 최근 7일간의 coffee 섭취 횟수 계산
        int coffeeCount = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            List<Coffee> dailyCoffee = coffeeRepository.findAllByUserIdAndDrinkDate(userId, date);
            coffeeCount += dailyCoffee.size();
        }
        
        // 주간 섭취 횟수에 따라 weekNumber 결정
        if (coffeeCount >= 14) { // 하루 평균 2회 이상
            return 7;
        } else if (coffeeCount >= 7) { // 하루 평균 1회 이상
            return 4;
        } else if (coffeeCount >= 2) { // 주간 2회 이상
            return 2;
        } else {
            return 0;
        }
    }
    
    private LocalDateTime calculateAlertnessEndTime(List<Coffee> coffeeList, LocalDateTime currentTime,
                                                    int age, int weekNumber, double tHalf, double weight) {
        // 각성도가 baseline(0.15) 이하로 떨어지는 시점을 찾음
        LocalDateTime checkTime = currentTime;
        double baseline = 0.15;
        
        // 최대 24시간 후까지 확인
        for (int hour = 0; hour < 24; hour++) {
            checkTime = checkTime.plusHours(1);
            
            // 해당 시점의 혈중 농도 계산
            double concentration = 0.0;
            for (Coffee coffee : coffeeList) {
                LocalDateTime drinkDateTime = LocalDateTime.of(coffee.getDrinkDate(), coffee.getDrinkTime());
                if (drinkDateTime.isAfter(checkTime)) {
                    continue;
                }
                double hoursElapsed = calculateHoursBetween(drinkDateTime, checkTime);
                if (hoursElapsed >= 0) {
                    double conc = CaffeineModel.concentrationWithAbsorption(
                            hoursElapsed,
                            coffee.getCaffeineAmount().doubleValue(),
                            weight,
                            tHalf
                    );
                    concentration += conc;
                }
            }
            
            // 각성도 계산
            double alertness = CaffeineModel.caffeineEffect(concentration, age, weekNumber, "alertness");
            
            // baseline 이하로 떨어지면 종료 시간 반환
            if (alertness <= baseline) {
                return checkTime;
            }
        }
        
        // 24시간 내에 baseline 이하로 떨어지지 않으면 null 반환
        return null;
    }

    private double calculateHoursBetween(LocalDateTime start, LocalDateTime end) {
        long seconds = java.time.Duration.between(start, end).getSeconds();
        return seconds / 3600.0;
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserException(USER_NOT_FOUND));
    }
}
