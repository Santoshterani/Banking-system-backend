package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.TransactionService;
import com.example.demo.Services.TransactionService.TransactionResult;
import com.example.demo.dto.request.TransactionRequests.DepositRequest;
import com.example.demo.dto.request.TransactionRequests.TransferRequest;
import com.example.demo.dto.request.TransactionRequests.WithdrawRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Transaction;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("hasAnyRole('CASHIER','CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','REGIONAL_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/deposit")
    public ApiResponse<Transaction> deposit(@Valid @RequestBody DepositRequest request) {
        return toResponse(transactionService.deposit(request.getAccountId(), request.getAmount()), "deposited");
    }

    @PreAuthorize("hasAnyRole('CASHIER','CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','REGIONAL_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/withdraw")
    public ApiResponse<Transaction> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return toResponse(transactionService.withdraw(request.getAccountId(), request.getAmount()), "withdrawn");
    }

    // Cashiers can only deposit/withdraw, not transfer - see requirements roadmap.
    @PreAuthorize("hasAnyRole('CUSTOMER_SERVICE_EXECUTIVE','BRANCH_MANAGER','REGIONAL_MANAGER','ADMIN','SUPER_ADMIN')")
    @PostMapping("/transfer")
    public ApiResponse<Transaction> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResult result = transactionService.transfer(
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());
        return toResponse(result, "transferred");
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<List<Transaction>> getTransactions(@PathVariable Long accountId) {
        return ApiResponse.success(transactionService.getTransactions(accountId));
    }

    private ApiResponse<Transaction> toResponse(TransactionResult result, String verb) {
        if (result.isPendingApproval()) {
            return ApiResponse.success(
                    "Amount exceeds your approval limit - request #" + result.getPendingApprovalId()
                            + " sent to your branch manager for approval",
                    null);
        }
        return ApiResponse.success("Amount " + verb + " successfully", result.getTransaction());
    }
}
