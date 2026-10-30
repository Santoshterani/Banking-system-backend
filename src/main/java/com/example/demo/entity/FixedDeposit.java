package com.example.demo.entity;

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
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // The account the principal + interest is paid into when this FD matures or is closed
    // early. Must belong to the same customer - enforced in FDService.
    @ManyToOne
    @JoinColumn(name = "beneficiary_account_id")
    private Account beneficiaryAccount;

    private Double principalAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private Double maturityAmount;

    // ACTIVE / MATURED / CLOSED
    private String status;

    private LocalDateTime startDate;

    public Long getFdId() { return fdId; }
    public void setFdId(Long fdId) { this.fdId = fdId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Account getBeneficiaryAccount() { return beneficiaryAccount; }
    public void setBeneficiaryAccount(Account beneficiaryAccount) { this.beneficiaryAccount = beneficiaryAccount; }
    public Double getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(Double principalAmount) { this.principalAmount = principalAmount; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public Double getMaturityAmount() { return maturityAmount; }
    public void setMaturityAmount(Double maturityAmount) { this.maturityAmount = maturityAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
}
