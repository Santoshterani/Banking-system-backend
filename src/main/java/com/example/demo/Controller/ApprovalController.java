package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.ApprovalService;
import com.example.demo.dto.request.RejectRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.ApprovalRequest;

@RestController
@RequestMapping("/admin/approval")
@PreAuthorize("hasAnyRole('BRANCH_MANAGER','REGIONAL_MANAGER','ADMIN','SUPER_ADMIN')")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @GetMapping("/pending")
    public ApiResponse<List<ApprovalRequest>> pending() {
        return ApiResponse.success(approvalService.listPending());
    }

    @PutMapping("/approve/{id}")
    public ApiResponse<ApprovalRequest> approve(@PathVariable Long id) {
        return ApiResponse.success("Request approved and executed", approvalService.approve(id));
    }

    @PutMapping("/reject/{id}")
    public ApiResponse<ApprovalRequest> reject(@PathVariable Long id, @RequestBody(required = false) RejectRequest request) {
        String remarks = request != null ? request.getRemarks() : null;
        return ApiResponse.success("Request rejected", approvalService.reject(id, remarks));
    }
}
