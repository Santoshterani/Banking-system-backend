package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class UPI {

    @Id
    private String upiId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime createdAt;
}
