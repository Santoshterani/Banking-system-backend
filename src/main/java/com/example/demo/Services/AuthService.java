package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.UserRepository;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${banking.security.max-failed-login-attempts:5}")
    private int maxFailedAttempts;

    @Value("${banking.security.account-lock-minutes:15}")
    private long lockMinutes;

    public String login(String email, String password) {

        Optional<User> maybeUser = repo.findByEmail(email);

        // Deliberately identical error for "no such user" and "wrong password" so callers
        // cannot enumerate valid emails by watching error messages differ.
        if (maybeUser.isEmpty()) {
            log.warn("Failed login attempt for unknown email: {}", email);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = maybeUser.get();

        if (isLocked(user)) {
            log.warn("Login attempt for locked account: {}", email);
            throw new InvalidCredentialsException(
                    "This account is temporarily locked due to repeated failed login attempts. Try again later.");
        }

        if (!encoder.matches(password, user.getPassword())) {
            registerFailedAttempt(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            log.warn("Login attempt for deactivated account: {}", email);
            throw new InvalidCredentialsException("This account has been deactivated. Contact your administrator.");
        }

        if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            repo.save(user);
        }

        log.info("Successful login for email: {}", email);

        return jwtUtil.generateToken(email, user.getRole().getRoleName());
    }

    private boolean isLocked(User user) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }

    private void registerFailedAttempt(User user) {

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxFailedAttempts) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
            log.warn("Account locked after {} failed attempts: {}", attempts, user.getEmail());
        } else {
            log.warn("Failed login attempt {}/{} for email: {}", attempts, maxFailedAttempts, user.getEmail());
        }

        repo.save(user);
        throw new InvalidCredentialsException("Invalid email or password");
    }
}
