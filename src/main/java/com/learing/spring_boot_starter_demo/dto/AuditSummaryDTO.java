package com.learing.spring_boot_starter_demo.dto;

import com.learing.spring_boot_starter_demo.model.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for audit summary statistics.
 * This is a separate top-level class so Hibernate can resolve it in JPQL queries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditSummaryDTO {
    private String entityName;
    private AuditLog.AuditAction action;
    private Long count;
}
