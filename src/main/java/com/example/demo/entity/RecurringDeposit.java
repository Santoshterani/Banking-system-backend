package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RecurringDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rdId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private double monthlyAmount;
    private double interestRate;
    private int durationMonths;
    private int paidMonths;

    private String status;

    private LocalDateTime createdAt;
}
