package practice.deploy.coffee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import practice.deploy.coffee.domain.Coffee;

import java.time.LocalDate;
import java.time.LocalTime;

public record CoffeeItemResponse(Long coffeeId,
                                 String name,
                                 @JsonFormat(pattern = "yyyy-MM-dd")
                                 LocalDate drinkDate,
                                 @JsonFormat(pattern = "HH:mm")
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
