package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AccountRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Branch id is required")
    private Long branchId;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CURRENT", message = "Account type must be SAVINGS or CURRENT")
    private String accountType;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
