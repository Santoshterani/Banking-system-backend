package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.AuthService;
import com.example.demo.Services.PasswordResetService;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ApiResponse<Void> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        String token = service.login(request.getEmail(), request.getPassword());

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);

        response.addCookie(cookie);

        return ApiResponse.success("Login successful", null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ApiResponse.success("Logged out", null);
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        return ApiResponse.success(new CurrentUserResponse(
                user.getName(), user.getEmail(), user.getRole().getRoleName(),
                user.getBranch() != null ? user.getBranch().getBranchId() : null,
                user.getBranch() != null ? user.getBranch().getName() : null));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestOtp(request.getEmail());
        return ApiResponse.success("If an account exists for this email, a reset code has been sent.", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ApiResponse.success("Password reset successfully. You can now log in with your new password.", null);
    }

    public static class CurrentUserResponse {
        private String name;
        private String email;
        private String role;
        private Long branchId;
        private String branchName;

        public CurrentUserResponse(String name, String email, String role, Long branchId, String branchName) {
            this.name = name;
            this.email = email;
            this.role = role;
            this.branchId = branchId;
            this.branchName = branchName;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public Long getBranchId() { return branchId; }
        public String getBranchName() { return branchName; }
    }
}
