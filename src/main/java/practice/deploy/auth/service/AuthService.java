package practice.deploy.auth.service;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.deploy.auth.dto.request.LoginRequest;
import practice.deploy.auth.dto.response.LoginResponse;
import practice.deploy.auth.util.JwtTokenProvider;
import practice.deploy.user.domain.User;
import practice.deploy.user.exception.UserException;
import practice.deploy.user.repository.UserRepository;

import static practice.deploy.auth.exception.errorcode.AuthErrorCode.INVALID_CREDENTIALS;
import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_ALREADY_EXISTS;
import static practice.deploy.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public void signup(LoginRequest request) {
        String username = request.username();

        if (userRepository.existsByUsername(username)) {
            throw new UserException(USER_ALREADY_EXISTS);
        }

        userRepository.save(
                User.basicLoginBuilder()
                        .username(username)
                        .password(passwordEncoder.encode(request.password()))
                        .buildBasicLogin());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                        .findByUsername(request.username())
                        .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UserException(INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.createToken(user.getId().toString());
        return LoginResponse.from(user.getUsername(), token);
    }
}
