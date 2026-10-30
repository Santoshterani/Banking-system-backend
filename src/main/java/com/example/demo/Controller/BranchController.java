package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.BranchService;
import com.example.demo.dto.request.BranchRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Branch;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/branch")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse<Branch> create(@Valid @RequestBody BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setIfscCode(request.getIfscCode());
        return ApiResponse.success("Branch created successfully", branchService.createBranch(branch));
    }

    @GetMapping("/all")
    public ApiResponse<List<Branch>> all() {
        return ApiResponse.success(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ApiResponse<Branch> get(@PathVariable Long id) {
        return ApiResponse.success(branchService.getBranch(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public ApiResponse<Branch> update(@PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        Branch update = new Branch();
        update.setName(request.getName());
        update.setAddress(request.getAddress());
        update.setIfscCode(request.getIfscCode());
        return ApiResponse.success("Branch updated successfully", branchService.updateBranch(id, update));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ApiResponse.success("Branch deleted successfully", null);
    }
}
