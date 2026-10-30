package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionRequests {

    public static class DepositRequest {

        @NotNull(message = "Account id is required")
        private Long accountId;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        private Double amount;

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }

    public static class WithdrawRequest extends DepositRequest {
    }

    public static class TransferRequest {

        @NotNull(message = "Sender account id is required")
        private Long fromAccountId;

        @NotNull(message = "Receiver account id is required")
        private Long toAccountId;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        private Double amount;

        public Long getFromAccountId() { return fromAccountId; }
        public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
        public Long getToAccountId() { return toAccountId; }
        public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
}
