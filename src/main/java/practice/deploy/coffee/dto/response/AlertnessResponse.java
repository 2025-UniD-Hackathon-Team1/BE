package practice.deploy.coffee.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AlertnessResponse(
        Double currentAlertness,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime alertnessEndTime
) {}

