package practice.deploy.user.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practice.deploy.user.domain.User;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;

import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user =
                userRepository
                        .findUserById(Long.parseLong(id))
                        .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        return new CustomUserDetails(user.getId(), user.getUsername(), user.getRole().name());
    }
}