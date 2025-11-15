package practice.deploy.coffee.dto.response;

import java.util.List;

public record CoffeeListResponse(
        List<CoffeeItemResponse> coffeeItemResponseList
) {
    public static CoffeeListResponse from(List<CoffeeItemResponse> coffeeItemList) {
        return new CoffeeListResponse(coffeeItemList);
    }
}
