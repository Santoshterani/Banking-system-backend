package com.example.demo.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Repositories.RoleRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;

/**
 * Runs once on startup. Seeds the fixed set of roles this system understands (RBAC checks
 * throughout the codebase are hardcoded against these exact names). Also seeds a bootstrap
 * SUPER_ADMIN if the users table is empty, since without one nobody could ever log in to
 * create the first real admin.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final List<String> ROLE_NAMES = List.of(
            "SUPER_ADMIN",
            "ADMIN",
            "REGIONAL_MANAGER",
            "BRANCH_MANAGER",
            "CASHIER",
            "CUSTOMER_SERVICE_EXECUTIVE",
            "LOAN_OFFICER",
            "KYC_OFFICER",
            "AUDITOR"
    );

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${banking.bootstrap.admin-email}")
    private String bootstrapAdminEmail;

    @Value("${banking.bootstrap.admin-password}")
    private String bootstrapAdminPassword;

    @Override
    public void run(String... args) {

        for (String roleName : ROLE_NAMES) {
            roleRepository.findByRoleName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setRoleName(roleName);
                log.info("Seeding role: {}", roleName);
                return roleRepository.save(role);
            });
        }

        if (userRepository.count() == 0) {

            Role superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN")
                    .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role missing after seeding"));

            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail(bootstrapAdminEmail);
            admin.setPassword(encoder.encode(bootstrapAdminPassword));
            admin.setRole(superAdminRole);
            admin.setActive(true);

            userRepository.save(admin);

            log.warn("No users found - seeded a bootstrap SUPER_ADMIN account ({}). " +
                    "Log in and change this password immediately.", bootstrapAdminEmail);
        }
    }
}
