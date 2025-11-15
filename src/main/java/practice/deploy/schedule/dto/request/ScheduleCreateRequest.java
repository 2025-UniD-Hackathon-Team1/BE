package practice.deploy.schedule.dto.request;

import java.time.LocalDate;

public record ScheduleCreateRequest(
        String time,
        LocalDate date,
        String name
){}