package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * A pending action that exceeds a cashier/executive's authority and needs a branch manager
 * (or above) to sign off before it actually executes. Covers large cash withdrawal/deposit
 * and large transfers. (Loan approval already has its own PENDING/APPROVED/REJECTED
 * workflow directly on the Loan entity, so it isn't routed through here.)
 */
@Entity
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;

    // WITHDRAWAL / DEPOSIT / TRANSFER
    private String type;

    // e.g. "accountId=12" or "fromAccountId=12;toAccountId=45"
    @Column(length = 1000)
    private String payload;

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "decided_by")
    private User decidedBy;

    // PENDING / APPROVED / REJECTED
    private String status;

    @Column(length = 500)
    private String remarks;

    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;

    public Long getApprovalId() { return approvalId; }
    public void setApprovalId(Long approvalId) { this.approvalId = approvalId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }
    public User getDecidedBy() { return decidedBy; }
    public void setDecidedBy(User decidedBy) { this.decidedBy = decidedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
}
