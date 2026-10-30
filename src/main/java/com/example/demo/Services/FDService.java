package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.FDRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.calculator.InterestCalculatorService;
import com.example.demo.dto.response.InterestCalculationResult;
import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.FixedDeposit;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class FDService {

    private static final Logger log = LoggerFactory.getLogger(FDService.class);

    @Autowired
    private FDRepository fdRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InterestCalculatorService interestCalculatorService;

    @Autowired
    private SecurityUtils securityUtils;

    public FixedDeposit createFD(FixedDeposit fd, Long beneficiaryAccountId) {

        if (fd.getCustomer() == null || fd.getCustomer().getCustomerId() == null) {
            throw new BadRequestException("Customer is required to open a Fixed Deposit");
        }
        if (fd.getPrincipalAmount() == null || fd.getPrincipalAmount() <= 0) {
            throw new BadRequestException("Principal amount must be greater than zero");
        }
        if (fd.getInterestRate() == null || fd.getInterestRate() <= 0) {
            throw new BadRequestException("Interest rate must be greater than zero");
        }
        if (fd.getTenureMonths() == null || fd.getTenureMonths() <= 0) {
            throw new BadRequestException("Tenure must be greater than zero months");
        }

        Customer customer = customerRepository
                .findById(fd.getCustomer().getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", fd.getCustomer().getCustomerId()));

        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());

        Account beneficiaryAccount = resolveBeneficiaryAccount(beneficiaryAccountId, customer.getCustomerId());

        fd.setCustomer(customer);
        fd.setBeneficiaryAccount(beneficiaryAccount);
        fd.setStartDate(LocalDateTime.now());
        fd.setStatus("ACTIVE");

        InterestCalculationResult result = interestCalculatorService.calculateSimpleInterest(
                fd.getPrincipalAmount(), fd.getInterestRate(), fd.getTenureMonths());
        fd.setMaturityAmount(result.getMaturityAmount());

        FixedDeposit saved = fdRepository.save(fd);
        log.info("FD {} created for customer {} - principal={}, maturity={}, beneficiaryAccount={}",
                saved.getFdId(), customer.getCustomerId(), saved.getPrincipalAmount(), saved.getMaturityAmount(),
                beneficiaryAccount.getAccountNumber());
        return saved;
    }

    public List<FixedDeposit> getAllFD() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return fdRepository.findAll();
        }
        return fdRepository.findByCustomer_Branch_BranchId(branchFilter);
    }

    public List<FixedDeposit> getCustomerFD(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());
        return fdRepository.findByCustomerCustomerId(customerId);
    }

    /** Staff-initiated early/on-schedule closure: credits the beneficiary account immediately. */
    @Auditable("CLOSE_FD")
    public FixedDeposit closeFD(Long id) {

        FixedDeposit fd = fdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit", id));

        securityUtils.assertBranchAccess(fd.getCustomer().getBranch().getBranchId());

        if (!"ACTIVE".equals(fd.getStatus())) {
            throw new BadRequestException("Only an active Fixed Deposit can be closed");
        }

        creditBeneficiary(fd);
        fd.setStatus("CLOSED");

        FixedDeposit saved = fdRepository.save(fd);
        log.info("FD {} closed and {} credited to account {}", id, fd.getMaturityAmount(), fd.getBeneficiaryAccount().getAccountNumber());
        return saved;
    }

    /** Called by the nightly MaturityJob once tenure has elapsed - same money movement as a
     *  manual close, just triggered automatically with no staff action required. */
    @Auditable("FD_AUTO_MATURITY_CREDIT")
    public void creditOnMaturity(FixedDeposit fd) {
        creditBeneficiary(fd);
        fd.setStatus("MATURED");
        fdRepository.save(fd);
    }

    private void creditBeneficiary(FixedDeposit fd) {
        if (fd.getBeneficiaryAccount() == null) {
            // Data created before beneficiary accounts were required - can't auto-credit, needs manual handling.
            throw new BadRequestException("This Fixed Deposit has no beneficiary account on record. Contact an administrator.");
        }
        transactionService.executeDeposit(fd.getBeneficiaryAccount().getAccountId(), fd.getMaturityAmount());
    }

    private Account resolveBeneficiaryAccount(Long accountId, Long customerId) {
        if (accountId == null) {
            throw new BadRequestException("A beneficiary account is required to receive the maturity payout");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.getCustomer().getCustomerId().equals(customerId)) {
            throw new BadRequestException("The beneficiary account must belong to the same customer opening the Fixed Deposit");
        }
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new BadRequestException("The beneficiary account must be ACTIVE");
        }
        return account;
    }
}
