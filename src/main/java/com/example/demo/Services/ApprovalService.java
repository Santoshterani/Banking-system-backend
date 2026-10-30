package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.ApprovalRequestRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.entity.ApprovalRequest;
import com.example.demo.entity.Branch;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    @Autowired
    private ApprovalRequestRepository approvalRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private TransactionService transactionService;

    public ApprovalRequest createApproval(String type, Map<String, String> payloadFields,
                                           Double amount, Branch branch, User requestedBy) {

        String payload = payloadFields.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";"));

        ApprovalRequest approval = new ApprovalRequest();
        approval.setType(type);
        approval.setPayload(payload);
        approval.setAmount(amount);
        approval.setBranch(branch);
        approval.setRequestedBy(requestedBy);
        approval.setStatus("PENDING");
        approval.setCreatedAt(LocalDateTime.now());

        ApprovalRequest saved = approvalRepository.save(approval);
        log.info("Approval request created: id={}, type={}, amount={}, branch={}, requestedBy={}",
                saved.getApprovalId(), type, amount, branch.getBranchId(), requestedBy.getEmail());
        return saved;
    }

    public List<ApprovalRequest> listPending() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return approvalRepository.findByStatus("PENDING");
        }
        return approvalRepository.findByBranch_BranchIdAndStatus(branchFilter, "PENDING");
    }

    @Auditable("APPROVE_LARGE_TRANSACTION")
    public ApprovalRequest approve(Long id) {

        ApprovalRequest approval = getPendingOrThrow(id);
        securityUtils.assertBranchAccess(approval.getBranch().getBranchId());

        Map<String, String> fields = parsePayload(approval.getPayload());

        switch (approval.getType()) {
            case "WITHDRAWAL" ->
                transactionService.executeWithdraw(Long.valueOf(fields.get("accountId")), approval.getAmount());
            case "DEPOSIT" ->
                transactionService.executeDeposit(Long.valueOf(fields.get("accountId")), approval.getAmount());
            case "TRANSFER" ->
                transactionService.executeTransfer(
                        Long.valueOf(fields.get("fromAccountId")),
                        Long.valueOf(fields.get("toAccountId")),
                        approval.getAmount());
            default -> throw new BadRequestException("Unknown approval type: " + approval.getType());
        }

        approval.setStatus("APPROVED");
        approval.setDecidedBy(securityUtils.getCurrentUser());
        approval.setDecidedAt(LocalDateTime.now());

        ApprovalRequest saved = approvalRepository.save(approval);
        log.info("Approval {} approved and executed by {}", id, saved.getDecidedBy().getEmail());
        return saved;
    }

    @Auditable("REJECT_LARGE_TRANSACTION")
    public ApprovalRequest reject(Long id, String remarks) {

        ApprovalRequest approval = getPendingOrThrow(id);
        securityUtils.assertBranchAccess(approval.getBranch().getBranchId());

        approval.setStatus("REJECTED");
        approval.setRemarks(remarks);
        approval.setDecidedBy(securityUtils.getCurrentUser());
        approval.setDecidedAt(LocalDateTime.now());

        ApprovalRequest saved = approvalRepository.save(approval);
        log.info("Approval {} rejected by {}", id, saved.getDecidedBy().getEmail());
        return saved;
    }

    private ApprovalRequest getPendingOrThrow(Long id) {
        ApprovalRequest approval = approvalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Approval request", id));

        if (!"PENDING".equals(approval.getStatus())) {
            throw new BadRequestException("This approval request has already been decided");
        }
        return approval;
    }

    private Map<String, String> parsePayload(String payload) {
        return java.util.Arrays.stream(payload.split(";"))
                .map(pair -> pair.split("=", 2))
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
    }
}
