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
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Same purpose as FixedDeposit.beneficiaryAccount - where maturity proceeds land.
    @ManyToOne
    @JoinColumn(name = "beneficiary_account_id")
    private Account beneficiaryAccount;

    private Double monthlyDeposit;
    private Double interestRate;
    private Integer tenureMonths;
    private Double maturityAmount;

    // ACTIVE / MATURED / CLOSED
    private String status;

    private LocalDateTime createdDate;

    public Long getRdId() { return rdId; }
    public void setRdId(Long rdId) { this.rdId = rdId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Account getBeneficiaryAccount() { return beneficiaryAccount; }
    public void setBeneficiaryAccount(Account beneficiaryAccount) { this.beneficiaryAccount = beneficiaryAccount; }
    public Double getMonthlyDeposit() { return monthlyDeposit; }
    public void setMonthlyDeposit(Double monthlyDeposit) { this.monthlyDeposit = monthlyDeposit; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public Double getMaturityAmount() { return maturityAmount; }
    public void setMaturityAmount(Double maturityAmount) { this.maturityAmount = maturityAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
