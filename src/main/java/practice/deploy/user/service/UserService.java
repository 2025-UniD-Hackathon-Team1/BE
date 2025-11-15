package practice.deploy.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.deploy.user.domain.User;
import practice.deploy.user.dto.request.CreateUserInfoRequest;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;

import java.time.LocalTime;

import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createUserInfo(Long userId, CreateUserInfoRequest request){
        User user = findUserOrThrow(userId);

        LocalTime sleepTime = LocalTime.parse(request.sleepTime());
        LocalTime wakeupTime = LocalTime.parse(request.wakeupTime());

        user.insertUserInfo(
                request.gender(),
                request.age(),
                sleepTime,
                wakeupTime,
                request.importantPeriod(),
                request.experience(),
                request.sensitivityLevel()
                );
    }

    private User findUserOrThrow(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new UserException(USER_NOT_FOUND));
    }
}
