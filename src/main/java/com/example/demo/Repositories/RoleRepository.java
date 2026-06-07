package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.demo.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);
}
