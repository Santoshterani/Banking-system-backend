package com.example.demo.calculator;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.InterestCalculationResult;
import com.example.demo.exception.BadRequestException;

/**
 * Pure calculation, no persistence - lets staff preview FD/RD/loan numbers before committing
 * to anything, and is the single source of truth for the maturity-amount formulas also used
 * internally by FDService/RDService when a deposit is actually opened (kept in sync here
 * rather than duplicated in each service).
 */
@Service
public class InterestCalculatorService {

    public InterestCalculationResult calculateSimpleInterest(Double principal, Double annualRatePercent, Integer tenureMonths) {
        validate(principal, annualRatePercent, tenureMonths);

        double years = tenureMonths / 12.0;
        double interest = principal * (annualRatePercent / 100) * years;
        double maturity = principal + interest;

        return new InterestCalculationResult(principal, annualRatePercent, tenureMonths, "SIMPLE",
                round(interest), round(maturity));
    }

    public InterestCalculationResult calculateCompoundInterest(Double principal, Double annualRatePercent, Integer tenureMonths) {
        validate(principal, annualRatePercent, tenureMonths);

        // Compounded quarterly - the common convention for Indian bank FDs.
        double ratePerQuarter = annualRatePercent / 100 / 4;
        double quarters = tenureMonths / 3.0;
        double maturity = principal * Math.pow(1 + ratePerQuarter, quarters);
        double interest = maturity - principal;

        return new InterestCalculationResult(principal, annualRatePercent, tenureMonths, "COMPOUND_QUARTERLY",
                round(interest), round(maturity));
    }

    /** Recurring Deposit maturity: each of the (tenureMonths) installments earns interest for
     *  a progressively shorter remaining period - this is the standard RD maturity formula. */
    public InterestCalculationResult calculateRecurringDepositMaturity(Double monthlyDeposit, Double annualRatePercent, Integer tenureMonths) {
        validate(monthlyDeposit, annualRatePercent, tenureMonths);

        double totalDeposited = monthlyDeposit * tenureMonths;
        double interest = totalDeposited * (annualRatePercent / 100) * tenureMonths / 12.0;
        double maturity = totalDeposited + interest;

        return new InterestCalculationResult(monthlyDeposit, annualRatePercent, tenureMonths, "RECURRING_DEPOSIT",
                round(interest), round(maturity));
    }

    private void validate(Double amount, Double rate, Integer tenureMonths) {
        if (amount == null || amount <= 0) throw new BadRequestException("Amount must be greater than zero");
        if (rate == null || rate <= 0) throw new BadRequestException("Interest rate must be greater than zero");
        if (tenureMonths == null || tenureMonths <= 0) throw new BadRequestException("Tenure must be greater than zero months");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
