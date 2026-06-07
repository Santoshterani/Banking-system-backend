package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // WEEKLY / MONTHLY

    private double totalDeposits;
    private double totalWithdrawals;
    private long totalTransactions;

    private LocalDate generatedDate;
}
