package com.example.demo.dto.response;

public class InterestCalculationResult {

    private Double principal;
    private Double interestRate;
    private Integer tenureMonths;
    private String interestType;
    private Double interestAmount;
    private Double maturityAmount;

    public InterestCalculationResult(Double principal, Double interestRate, Integer tenureMonths,
                                      String interestType, Double interestAmount, Double maturityAmount) {
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.interestType = interestType;
        this.interestAmount = interestAmount;
        this.maturityAmount = maturityAmount;
    }

    public Double getPrincipal() { return principal; }
    public Double getInterestRate() { return interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public String getInterestType() { return interestType; }
    public Double getInterestAmount() { return interestAmount; }
    public Double getMaturityAmount() { return maturityAmount; }
}
