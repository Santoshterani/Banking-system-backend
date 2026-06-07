package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class FixedDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fdId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private double amount;
    private double interestRate;

    private LocalDate startDate;
    private LocalDate maturityDate;

    private String status;

    private LocalDateTime createdAt;
}
