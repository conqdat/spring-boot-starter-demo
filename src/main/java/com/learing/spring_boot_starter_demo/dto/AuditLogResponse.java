package com.learing.spring_boot_starter_demo.dto;

import com.learing.spring_boot_starter_demo.model.AuditLog;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogResponse {

    private Long id;
    private String entityName;
    private Long entityId;
    private AuditLog.AuditAction action;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private LocalDateTime changedAt;

    public static AuditLogResponse fromEntity(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .action(auditLog.getAction())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .changedBy(auditLog.getChangedBy())
                .changedAt(auditLog.getChangedAt())
                .build();
    }
}
