package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class RDPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "rd_id")
    private RecurringDeposit rd;

    private double amount;
    private LocalDate paymentDate;
    private String status;
}