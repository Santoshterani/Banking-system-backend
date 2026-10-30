package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AccountUpdateRequest {

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CURRENT", message = "Account type must be SAVINGS or CURRENT")
    private String accountType;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|FROZEN|CLOSED", message = "Status must be ACTIVE, FROZEN or CLOSED")
    private String status;

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
