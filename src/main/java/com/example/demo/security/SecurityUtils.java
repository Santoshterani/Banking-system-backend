package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.Repositories.UserRepository;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * Central place to answer "who is making this request, and what are they allowed to touch"
 * so every service doesn't have to re-implement SecurityContext lookups + branch comparisons.
 */
@Component
public class SecurityUtils {

    // Roles that operate bank-wide and are never restricted to a single branch.
    private static final java.util.Set<String> BANK_WIDE_ROLES = java.util.Set.of(
            "SUPER_ADMIN", "ADMIN", "REGIONAL_MANAGER", "AUDITOR"
    );

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user in context");
        }

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    public boolean isBankWide(User user) {
        return BANK_WIDE_ROLES.contains(user.getRole().getRoleName());
    }

    /**
     * Throws if the current user is branch-scoped and the branch they're assigned to
     * does not match the branch of the resource they're trying to touch.
     * Bank-wide roles (admin/super-admin/regional-manager/auditor) always pass.
     */
    public void assertBranchAccess(Long resourceBranchId) {

        User current = getCurrentUser();

        if (isBankWide(current)) {
            return;
        }

        if (current.getBranch() == null || resourceBranchId == null
                || !current.getBranch().getBranchId().equals(resourceBranchId)) {
            throw new com.example.demo.exception.AccessDeniedForBranchException(
                    "You do not have access to this branch's data");
        }
    }

    /**
     * Returns the branch id a branch-scoped user is restricted to, or null for bank-wide
     * roles (meaning "no restriction, see everything").
     */
    public Long getBranchFilter() {
        User current = getCurrentUser();
        if (isBankWide(current)) {
            return null;
        }
        if (current.getBranch() == null) {
            throw new com.example.demo.exception.AccessDeniedForBranchException(
                    "Your account is not assigned to a branch. Contact your administrator.");
        }
        return current.getBranch().getBranchId();
    }
}
