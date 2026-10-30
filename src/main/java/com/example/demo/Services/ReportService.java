package com.example.demo.Services;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.ApprovalRequestRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.KYCRepository;
import com.example.demo.Repositories.LoanRepository;
import com.example.demo.Repositories.TransactionRepository;
import com.example.demo.dto.response.DashboardStats;
import com.example.demo.security.SecurityUtils;

@Service
public class ReportService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private KYCRepository kycRepository;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public DashboardStats getDashboardStats() {

        Long branchFilter = securityUtils.getBranchFilter();

        LocalDateTime startOfDay = LocalDateTime.of(java.time.LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(java.time.LocalDate.now(), LocalTime.MAX);

        DashboardStats stats = new DashboardStats();

        if (branchFilter == null) {
            stats.setTotalCustomers(customerRepository.count());
            stats.setTotalAccounts(accountRepository.count());
            stats.setTotalBalance(accountRepository.sumAllBalances());
            stats.setPendingLoans(loanRepository.countByStatus("PENDING"));
            stats.setPendingKyc(kycRepository.countByStatus("PENDING"));
            stats.setPendingApprovals(approvalRequestRepository.findByStatus("PENDING").size());
            stats.setTransactionsToday(transactionRepository.countByTransactionDateBetween(startOfDay, endOfDay));
            stats.setDepositsToday(transactionRepository.sumAmountByTypeBetween("DEPOSIT", startOfDay, endOfDay));
            stats.setWithdrawalsToday(transactionRepository.sumAmountByTypeBetween("WITHDRAW", startOfDay, endOfDay));
        } else {
            stats.setTotalCustomers(customerRepository.countByBranch_BranchId(branchFilter));
            stats.setTotalAccounts(accountRepository.countByBranch_BranchId(branchFilter));
            stats.setTotalBalance(accountRepository.sumBalancesByBranch(branchFilter));
            stats.setPendingLoans(loanRepository.countByCustomer_Branch_BranchIdAndStatus(branchFilter, "PENDING"));
            stats.setPendingKyc(kycRepository.countByCustomer_Branch_BranchIdAndStatus(branchFilter, "PENDING"));
            stats.setPendingApprovals(approvalRequestRepository.findByBranch_BranchIdAndStatus(branchFilter, "PENDING").size());
            stats.setTransactionsToday(transactionRepository.countByAccount_Branch_BranchIdAndTransactionDateBetween(branchFilter, startOfDay, endOfDay));
            stats.setDepositsToday(transactionRepository.sumAmountByTypeAndBranchBetween("DEPOSIT", branchFilter, startOfDay, endOfDay));
            stats.setWithdrawalsToday(transactionRepository.sumAmountByTypeAndBranchBetween("WITHDRAW", branchFilter, startOfDay, endOfDay));
        }

        return stats;
    }
}
