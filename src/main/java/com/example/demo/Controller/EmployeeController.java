package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.EmployeeService;
import com.example.demo.dto.request.EmployeeRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.User;

import jakarta.validation.Valid;

/**
 * ADMIN / SUPER_ADMIN can manage staff anywhere. BRANCH_MANAGER can manage staff within
 * their own branch only (enforced in EmployeeService via SecurityUtils.assertBranchAccess).
 */
@RestController
@RequestMapping("/admin/employee")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','BRANCH_MANAGER')")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    public ApiResponse<User> create(@Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.success("Employee created successfully", employeeService.createEmployee(request));
    }

    @GetMapping("/all")
    public ApiResponse<List<User>> all() {
        return ApiResponse.success(employeeService.getAllEmployees());
    }

    @GetMapping("/page")
    public ApiResponse<Page<User>> page(Pageable pageable) {
        return ApiResponse.success(employeeService.getAllEmployees(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> get(@PathVariable Long id) {
        return ApiResponse.success(employeeService.getEmployee(id));
    }

    @PutMapping("/update/{id}")
    public ApiResponse<User> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.success("Employee updated successfully", employeeService.updateEmployee(id, request));
    }

    @PutMapping("/deactivate/{id}")
    public ApiResponse<User> deactivate(@PathVariable Long id) {
        return ApiResponse.success("Employee deactivated", employeeService.setActive(id, false));
    }

    @PutMapping("/activate/{id}")
    public ApiResponse<User> activate(@PathVariable Long id) {
        return ApiResponse.success("Employee activated", employeeService.setActive(id, true));
    }
}
