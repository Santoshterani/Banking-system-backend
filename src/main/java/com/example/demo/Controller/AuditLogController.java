package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repositories.AuditLogRepository;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.AuditLog;

@RestController
@RequestMapping("/admin/audit-log")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','AUDITOR')")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public ApiResponse<Page<AuditLog>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLog> logs = auditLogRepository.findAllByOrderByTimestampDesc(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")));

        return ApiResponse.success(logs);
    }
}
