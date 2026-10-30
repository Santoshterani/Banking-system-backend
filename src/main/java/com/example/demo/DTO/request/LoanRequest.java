package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LoanRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotBlank(message = "Loan type is required")
    private String loanType;

    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be greater than zero")
    private Double loanAmount;

    @NotNull(message = "Interest rate is required")
    @Positive(message = "Interest rate must be greater than zero")
    private Double interestRate;

    @NotNull(message = "Tenure is required")
    @Positive(message = "Tenure must be greater than zero months")
    private Integer tenureMonths;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public Double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(Double loanAmount) { this.loanAmount = loanAmount; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
}
