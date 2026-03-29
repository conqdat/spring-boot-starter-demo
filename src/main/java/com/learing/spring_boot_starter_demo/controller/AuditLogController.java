package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.dto.AuditLogResponse;
import com.learing.spring_boot_starter_demo.dto.AuditSummaryDTO;
import com.learing.spring_boot_starter_demo.model.AuditLog;
import com.learing.spring_boot_starter_demo.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * GET /api/audit-logs - Get all audit logs with pagination
     */
    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "changedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLogResponse> logs = auditLogService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/audit-logs/entity/{entityName} - Get audit logs by entity name
     */
    @GetMapping("/entity/{entityName}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntityName(
            @PathVariable String entityName) {
        List<AuditLogResponse> logs = auditLogService.getAuditLogsByEntityName(entityName);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/audit-logs/entity/{entityName}/id/{entityId} - Get audit logs by entity ID
     */
    @GetMapping("/entity/{entityName}/id/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntityId(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        List<AuditLogResponse> logs = auditLogService.getAuditLogsByEntityId(entityId);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/audit-logs/action/{action} - Get audit logs by action type
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByAction(
            @PathVariable AuditLog.AuditAction action) {
        List<AuditLogResponse> logs = auditLogService.getAuditLogsByAction(action);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/audit-logs/user/{username} - Get audit logs by user
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());
        Page<AuditLogResponse> logs = auditLogService.getAuditLogsByUser(username, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/audit-logs/date-range - Get audit logs within a date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AuditLogResponse> logs = auditLogService.getAuditLogsByDateRange(start, end);
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/audit-logs/summary - Get audit summary for the last N days
     */
    @GetMapping("/summary")
    public ResponseEntity<List<AuditSummaryDTO>> getAuditSummary(
            @RequestParam(defaultValue = "7") int days) {
        List<AuditSummaryDTO> summary = auditLogService.getAuditSummary(days);
        return ResponseEntity.ok(summary);
    }
}
