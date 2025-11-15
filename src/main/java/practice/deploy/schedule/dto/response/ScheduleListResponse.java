package practice.deploy.schedule.dto.response;

import practice.deploy.schedule.entity.Schedule;

import java.util.List;

public record ScheduleListResponse(
        List<ScheduleItemResponse> scheduleItemResponseList
) {
    public static ScheduleListResponse from(List<ScheduleItemResponse> scheduleResponseList) {
        return new ScheduleListResponse(scheduleResponseList);
    }
}
