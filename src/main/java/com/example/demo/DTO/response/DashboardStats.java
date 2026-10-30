package com.example.demo.dto.response;

public class DashboardStats {

    private long totalCustomers;
    private long totalAccounts;
    private double totalBalance;
    private long pendingLoans;
    private long pendingKyc;
    private long pendingApprovals;
    private long transactionsToday;
    private double depositsToday;
    private double withdrawalsToday;

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getTotalAccounts() { return totalAccounts; }
    public void setTotalAccounts(long totalAccounts) { this.totalAccounts = totalAccounts; }
    public double getTotalBalance() { return totalBalance; }
    public void setTotalBalance(double totalBalance) { this.totalBalance = totalBalance; }
    public long getPendingLoans() { return pendingLoans; }
    public void setPendingLoans(long pendingLoans) { this.pendingLoans = pendingLoans; }
    public long getPendingKyc() { return pendingKyc; }
    public void setPendingKyc(long pendingKyc) { this.pendingKyc = pendingKyc; }
    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; }
    public long getTransactionsToday() { return transactionsToday; }
    public void setTransactionsToday(long transactionsToday) { this.transactionsToday = transactionsToday; }
    public double getDepositsToday() { return depositsToday; }
    public void setDepositsToday(double depositsToday) { this.depositsToday = depositsToday; }
    public double getWithdrawalsToday() { return withdrawalsToday; }
    public void setWithdrawalsToday(double withdrawalsToday) { this.withdrawalsToday = withdrawalsToday; }
}
