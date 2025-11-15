package practice.deploy.coffee.controller;

import com.example.demo.dto.CoffeeResponseDto;
import com.example.demo.dto.CoffeeSaveRequestDto;
import com.example.demo.service.CoffeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffee")
public class CoffeeController {
    private final CoffeeService coffeeService;

    @PostMapping
    public CoffeeResponse create(@RequestBody CoffeeRequest request){
        return coffeeService.save(request);
    }

    @GetMapping("/{id}")
    public CoffeeResponse getCoffeeByUser(@PathVariable Long userId){
        return coffeeService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<CoffeeResponse> getCoffeesByUser(@PathVariable Long userId){
        return coffeeService.getByUserId(userId);
    }
}
