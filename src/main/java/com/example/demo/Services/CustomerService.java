package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.dto.request.CustomerOnboardingRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Customer;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * The real onboarding path: a bank cannot have a customer with no account, so this is
     * the only way the frontend creates a new customer - one form, one transaction, their
     * first account opened in the same breath. If account creation fails for any reason,
     * the customer row is rolled back too, rather than leaving an orphan customer record.
     */
    @Transactional
    @Auditable("ONBOARD_CUSTOMER")
    public Account onboardCustomer(CustomerOnboardingRequest request) {

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setAadhaarNumber(request.getAadhaarNumber());
        customer.setPanNumber(request.getPanNumber());

        Branch branch = new Branch();
        branch.setBranchId(request.getBranchId());
        customer.setBranch(branch);

        Customer savedCustomer = createCustomer(customer);

        Account account = new Account();
        Customer customerRef = new Customer();
        customerRef.setCustomerId(savedCustomer.getCustomerId());
        account.setCustomer(customerRef);

        Branch branchRef = new Branch();
        branchRef.setBranchId(request.getBranchId());
        account.setBranch(branchRef);

        account.setAccountType(request.getAccountType());

        Account savedAccount = accountService.createAccount(account);

        log.info("Customer {} onboarded with account {}", savedCustomer.getCustomerId(), savedAccount.getAccountNumber());
        return savedAccount;
    }

    public Customer createCustomer(Customer customer) {

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A customer with email '" + customer.getEmail() + "' already exists");
        }
        if (customerRepository.findByPhone(customer.getPhone()).isPresent()) {
            throw new DuplicateResourceException("A customer with phone number '" + customer.getPhone() + "' already exists");
        }
        if (customerRepository.findByAadhaarNumber(customer.getAadhaarNumber()).isPresent()) {
            throw new DuplicateResourceException("A customer with this Aadhaar number already exists");
        }
        if (customerRepository.findByPanNumber(customer.getPanNumber()).isPresent()) {
            throw new DuplicateResourceException("A customer with this PAN number already exists");
        }

        Long branchId = customer.getBranch().getBranchId();

        Long restriction = securityUtils.getBranchFilter();
        if (restriction != null) {
            branchId = restriction;
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        customer.setBranch(branch);
        customer.setCreatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: id={}, email={}, branch={}", saved.getCustomerId(), saved.getEmail(), branchId);
        return saved;
    }

    public List<Customer> getAllCustomers() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return customerRepository.findAll();
        }
        return customerRepository.findByBranch_BranchId(branchFilter);
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return customerRepository.findAll(pageable);
        }
        return customerRepository.findByBranch_BranchId(branchFilter, pageable);
    }

    public Customer getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());
        return customer;
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {

        Customer customer = getCustomer(id);
        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPhone(updatedCustomer.getPhone());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setAadhaarNumber(updatedCustomer.getAadhaarNumber());
        customer.setPanNumber(updatedCustomer.getPanNumber());

        Customer saved = customerRepository.save(customer);
        log.info("Customer updated: id={}", id);
        return saved;
    }

    @Auditable("DELETE_CUSTOMER")
    public void deleteCustomer(Long id) {
        Customer customer = getCustomer(id);

        // Every customer has at least one account by construction now (see onboardCustomer),
        // so this is the common case, not an edge case - a clear error beats a raw FK
        // constraint violation bubbling up as a 500.
        long accountCount = accountRepository.countByCustomer_CustomerId(id);
        if (accountCount > 0) {
            throw new BadRequestException(
                    "This customer has " + accountCount + " account(s) on record. Close or reassign them before deleting the customer.");
        }

        customerRepository.delete(customer);
        log.info("Customer deleted: id={}", id);
    }
}
