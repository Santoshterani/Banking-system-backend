package com.example.demo.Services;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.BranchRepository;
import com.example.demo.Repositories.RoleRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.dto.request.EmployeeRequest;
import com.example.demo.entity.Branch;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private static final Set<String> BRANCH_SCOPED_ROLES = Set.of(
            "BRANCH_MANAGER", "CASHIER", "CUSTOMER_SERVICE_EXECUTIVE", "LOAN_OFFICER", "KYC_OFFICER"
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private SecurityUtils securityUtils;

    @Auditable("CREATE_EMPLOYEE")
    public User createEmployee(EmployeeRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A user with email '" + request.getEmail() + "' already exists");
        }

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRole()));

        Branch branch = resolveBranch(request.getRole(), request.getBranchId());

        if (branch != null) {
            securityUtils.assertBranchAccess(branch.getBranchId());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(role);
        user.setBranch(branch);
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("Employee created: id={}, email={}, role={}, branch={}",
                saved.getId(), saved.getEmail(), request.getRole(), request.getBranchId());
        return saved;
    }

    public List<User> getAllEmployees() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return userRepository.findAll();
        }
        return userRepository.findByBranch_BranchId(branchFilter);
    }

    public Page<User> getAllEmployees(Pageable pageable) {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByBranch_BranchId(branchFilter, pageable);
    }

    public User getEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        if (user.getBranch() != null) {
            securityUtils.assertBranchAccess(user.getBranch().getBranchId());
        }
        return user;
    }

    @Auditable("UPDATE_EMPLOYEE")
    public User updateEmployee(Long id, EmployeeRequest request) {

        User user = getEmployee(id);

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.getRole()));

        Branch branch = resolveBranch(request.getRole(), request.getBranchId());
        if (branch != null) {
            securityUtils.assertBranchAccess(branch.getBranchId());
        }

        user.setName(request.getName());
        user.setRole(role);
        user.setBranch(branch);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        log.info("Employee updated: id={}", id);
        return saved;
    }

    @Auditable("SET_EMPLOYEE_ACTIVE")
    public User setActive(Long id, boolean active) {
        User user = getEmployee(id);
        user.setActive(active);
        User saved = userRepository.save(user);
        log.info("Employee {} {}", id, active ? "activated" : "deactivated");
        return saved;
    }

    private Branch resolveBranch(String roleName, Long branchId) {

        boolean requiresBranch = BRANCH_SCOPED_ROLES.contains(roleName);
        if (!requiresBranch) {
            return null;
        }
        if (branchId == null) {
            throw new BadRequestException("Role '" + roleName + "' requires a branch to be assigned");
        }
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));
    }
}
