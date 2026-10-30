package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.PasswordResetOtp;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    List<PasswordResetOtp> findByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
}
