package com.example.demo.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

/**
 * Every login identity in the system - customer-facing staff and admins alike.
 *
 * NOTE: this used to coexist with a separate, unused "Employee" entity that duplicated
 * name/email/password/role/branch. That duplication was a landmine (two tables that could
 * silently drift out of sync, and nothing decided which one was authoritative for login).
 * Consolidated here: an "employee" IS a User with a branch assigned. ADMIN / SUPER_ADMIN /
 * REGIONAL_MANAGER / AUDITOR are not tied to a single branch, so branch is nullable.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Null for roles that operate bank-wide (ADMIN, SUPER_ADMIN, REGIONAL_MANAGER, AUDITOR).
    // Required for branch-scoped roles (BRANCH_MANAGER, CASHIER, CUSTOMER_SERVICE_EXECUTIVE,
    // LOAN_OFFICER, KYC_OFFICER).
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private boolean active = true;

    private int failedLoginAttempts = 0;

    private LocalDateTime lockedUntil;

    private LocalDateTime createdAt;

    public User() {
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
