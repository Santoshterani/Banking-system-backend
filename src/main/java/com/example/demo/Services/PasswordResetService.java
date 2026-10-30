package com.example.demo.Services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.PasswordResetOtpRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.entity.PasswordResetOtp;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetOtpRepository otpRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private NotificationService notificationService;

    @Value("${banking.otp.expiry-minutes:10}")
    private long otpExpiryMinutes;

    public void requestOtp(String email) {

        // Always behave the same way whether the email exists or not, so this endpoint
        // can't be used to enumerate valid employee email addresses.
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("Password reset requested for unknown email: {}", email);
            return;
        }

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));

        PasswordResetOtp entity = new PasswordResetOtp();
        entity.setEmail(email);
        entity.setOtpHash(encoder.encode(otp));
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        entity.setUsed(false);
        entity.setCreatedAt(LocalDateTime.now());
        otpRepository.save(entity);

        notificationService.sendRawMail(email, "Password Reset Code",
                "Your password reset code is: " + otp + "\nThis code expires in "
                        + otpExpiryMinutes + " minutes. If you did not request this, you can ignore this email.");

        log.info("Password reset OTP issued for: {}", email);
    }

    public void resetPassword(String email, String otp, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or OTP"));

        List<PasswordResetOtp> candidates = otpRepository.findByEmailAndUsedFalseOrderByCreatedAtDesc(email);

        PasswordResetOtp match = candidates.stream()
                .filter(o -> o.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(o -> encoder.matches(otp, o.getOtpHash()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        match.setUsed(true);
        otpRepository.save(match);

        user.setPassword(encoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        log.info("Password reset completed for: {}", email);
    }
}
