package practice.deploy.report.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DailyCaffeineLog(
        LocalDate date,
        Long totalCaffeine,
        LocalTime lastDrinkTime,
        LocalTime sleepTime,
        double intervalHours,
        boolean hasPalpitation
) {
}
