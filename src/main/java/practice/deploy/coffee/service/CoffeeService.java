package practice.deploy.coffee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import practice.deploy.coffee.domain.Coffee;
import practice.deploy.coffee.dto.request.CoffeeRequest;
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
        User user = findUserOrThrow(userId);

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
        LocalDateTime graphEndTime = currentTime;

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

    private double calculateHoursBetween(LocalDateTime start, LocalDateTime end) {
        long seconds = java.time.Duration.between(start, end).getSeconds();
        return seconds / 3600.0;
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserException(USER_NOT_FOUND));
    }
}
