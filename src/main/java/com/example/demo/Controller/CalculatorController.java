package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.calculator.InterestCalculatorService;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.InterestCalculationResult;

/**
 * Stateless preview calculator - any authenticated staff member can use it to show a
 * customer "if you deposit X for Y months at Z%, you'll get this much back" before anything
 * is actually created. Does not touch the database.
 */
@RestController
@RequestMapping("/admin/calculator")
public class CalculatorController {

    @Autowired
    private InterestCalculatorService calculatorService;

    @GetMapping("/fd")
    public ApiResponse<InterestCalculationResult> fixedDeposit(
            @RequestParam Double principal, @RequestParam Double rate, @RequestParam Integer tenureMonths) {
        return ApiResponse.success(calculatorService.calculateSimpleInterest(principal, rate, tenureMonths));
    }

    @GetMapping("/rd")
    public ApiResponse<InterestCalculationResult> recurringDeposit(
            @RequestParam Double monthlyDeposit, @RequestParam Double rate, @RequestParam Integer tenureMonths) {
        return ApiResponse.success(calculatorService.calculateRecurringDepositMaturity(monthlyDeposit, rate, tenureMonths));
    }

    @GetMapping("/loan-emi")
    public ApiResponse<Double> loanEmi(
            @RequestParam Double principal, @RequestParam Double annualRate, @RequestParam Integer tenureMonths) {
        double monthlyRate = annualRate / (12 * 100);
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths))
                / (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return ApiResponse.success(Math.round(emi * 100.0) / 100.0);
    }
}
