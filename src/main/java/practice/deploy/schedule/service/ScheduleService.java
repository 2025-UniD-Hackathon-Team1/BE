package practice.deploy.schedule.service;

import org.springframework.transaction.annotation.Transactional;
import practice.deploy.schedule.dto.request.ScheduleCreateRequest;
import practice.deploy.schedule.dto.response.ScheduleItemResponse;
import practice.deploy.schedule.dto.response.ScheduleListResponse;
import practice.deploy.schedule.entity.Schedule;
import practice.deploy.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.deploy.user.domain.User;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;
import practice.deploy.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createSchedule(Long userId, ScheduleCreateRequest request) {

        User user = findUserOrThrow(userId);
        LocalTime time = LocalTime.parse(request.time());

        Schedule schedule = Schedule.builder()
                .name(request.name())
                .time(time)
                .date(request.date())
                .user(user)
                .build();

        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public ScheduleListResponse getScheduleList(Long userId, LocalDate time){
        User user = findUserOrThrow(userId);

        List<Schedule> scheduleList = scheduleRepository.findAllByUserIdAndDate(userId, time);
        List<ScheduleItemResponse> scheduleItemResponseList = scheduleList.stream()
                .map(ScheduleItemResponse::from)
                .collect(Collectors.toList());

        return ScheduleListResponse.from(scheduleItemResponseList);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserException(USER_NOT_FOUND));
    }
}
