package practice.deploy.coffee.dto.response;

import practice.deploy.coffee.domain.Coffee;

import java.time.LocalDate;
import java.time.LocalTime;

public record CoffeeItemResponse(Long coffeeId,
                                 String name,
                                 LocalDate drinkDate,
                                 LocalTime drinkTime,
                                 Long caffeineAmount) {
    public static CoffeeItemResponse from(Coffee coffee) {
        return new CoffeeItemResponse(
                coffee.getId(),
                coffee.getName(),
                coffee.getDrinkDate(),
                coffee.getDrinkTime(),
                coffee.getCaffeineAmount()
        );
    }
}
