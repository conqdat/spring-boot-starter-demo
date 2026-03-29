package com.learing.spring_boot_starter_demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learing.spring_boot_starter_demo.dto.AuditLogResponse;
import com.learing.spring_boot_starter_demo.dto.AuditSummaryDTO;
import com.learing.spring_boot_starter_demo.model.AuditLog;
import com.learing.spring_boot_starter_demo.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log a create action
     */
    public void logCreate(String entityName, Long entityId, Object newValue, String changedBy) {
        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(AuditLog.AuditAction.CREATE)
                .newValue(toJson(newValue))
                .changedBy(changedBy)
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Log an update action
     */
    public void logUpdate(String entityName, Long entityId, Object oldValue, Object newValue, String changedBy) {
        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(AuditLog.AuditAction.UPDATE)
                .oldValue(toJson(oldValue))
                .newValue(toJson(newValue))
                .changedBy(changedBy)
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Log a delete action
     */
    public void logDelete(String entityName, Long entityId, Object oldValue, String changedBy) {
        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(AuditLog.AuditAction.DELETE)
                .oldValue(toJson(oldValue))
                .changedBy(changedBy)
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Log a bulk update action
     */
    public void logBulkUpdate(String entityName, String changedBy, int affectedRows) {
        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(0L) // 0 for bulk operations
                .action(AuditLog.AuditAction.BULK_UPDATE)
                .newValue("{\"affectedRows\": " + affectedRows + "}")
                .changedBy(changedBy)
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Log a bulk delete action
     */
    public void logBulkDelete(String entityName, String changedBy, int affectedRows) {
        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(0L)
                .action(AuditLog.AuditAction.BULK_DELETE)
                .newValue("{\"affectedRows\": " + affectedRows + "}")
                .changedBy(changedBy)
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Get all audit logs with pagination
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(AuditLogResponse::fromEntity);
    }

    /**
     * Get audit logs by entity name
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByEntityName(String entityName) {
        return auditLogRepository.findByEntityNameOrderByChangedAtDesc(entityName)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs by entity ID
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByEntityId(Long entityId) {
        return auditLogRepository.findByEntityIdOrderByChangedAtDesc(entityId)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs by action type
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByAction(AuditLog.AuditAction action) {
        return auditLogRepository.findByActionOrderByChangedAtDesc(action)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs by user
     */
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByUser(String changedBy, Pageable pageable) {
        return auditLogRepository.findByChangedByOrderByChangedAtDesc(changedBy, pageable)
                .map(AuditLogResponse::fromEntity);
    }

    /**
     * Get audit logs within a date range
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByChangedAtBetweenOrderByChangedAtDesc(start, end)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get audit summary for the last N days
     */
    @Transactional(readOnly = true)
    public List<AuditSummaryDTO> getAuditSummary(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return auditLogRepository.getAuditSummary(since);
    }

    /**
     * Helper method to convert object to JSON string
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize\"}";
        }
    }
}
