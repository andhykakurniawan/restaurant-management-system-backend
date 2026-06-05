package com.example.restaurant_be.audit.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.audit.dto.AuditLogResponse;
import com.example.restaurant_be.audit.service.AuditLogService;
import com.example.restaurant_be.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(
                "Audit logs retrieved successfully",
                auditLogService.findAll()));
    }
}
