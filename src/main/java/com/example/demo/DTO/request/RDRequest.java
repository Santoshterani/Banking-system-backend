package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RDRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "A beneficiary account is required to receive the maturity payout")
    private Long accountId;

    @NotNull(message = "Monthly deposit is required")
    @Positive(message = "Monthly deposit must be greater than zero")
    private Double monthlyDeposit;

    @NotNull(message = "Interest rate is required")
    @Positive(message = "Interest rate must be greater than zero")
    private Double interestRate;

    @NotNull(message = "Tenure is required")
    @Positive(message = "Tenure must be greater than zero months")
    private Integer tenureMonths;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Double getMonthlyDeposit() { return monthlyDeposit; }
    public void setMonthlyDeposit(Double monthlyDeposit) { this.monthlyDeposit = monthlyDeposit; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
}
