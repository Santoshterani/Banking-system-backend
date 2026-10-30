package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repositories.RoleRepository;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Role;
import com.example.demo.exception.DuplicateResourceException;

@RestController
@RequestMapping("/admin/role")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class RoleController {

    @Autowired
    private RoleRepository repo;

    @PostMapping("/create")
    public ApiResponse<Role> create(@RequestBody Role role) {
        if (repo.findByRoleName(role.getRoleName()).isPresent()) {
            throw new DuplicateResourceException("Role '" + role.getRoleName() + "' already exists");
        }
        return ApiResponse.success("Role created successfully", repo.save(role));
    }
}
