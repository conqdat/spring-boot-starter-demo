package com.learing.spring_boot_starter_demo.listener;

import com.learing.spring_boot_starter_demo.model.AuditLog;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.repository.AuditLogRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JPA Entity Listener for automatically logging Todo entity changes.
 * This is a real-world pattern for audit trails without cluttering service code.
 */
@Component
@RequiredArgsConstructor
public class TodoAuditListener {

    private final AuditLogRepository auditLogRepository;

    /**
     * Called after a Todo is persisted (created)
     */
    @PostPersist
    public void afterCreate(Todo todo) {
        AuditLog auditLog = AuditLog.builder()
                .entityName("Todo")
                .entityId(todo.getId())
                .action(AuditLog.AuditAction.CREATE)
                .newValue(toJson(todo))
                .changedBy(getCurrentUser())
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Called after a Todo is updated
     */
    @PostUpdate
    public void afterUpdate(Todo todo) {
        AuditLog auditLog = AuditLog.builder()
                .entityName("Todo")
                .entityId(todo.getId())
                .action(AuditLog.AuditAction.UPDATE)
                .newValue(toJson(todo))
                .changedBy(getCurrentUser())
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Called after a Todo is removed (deleted)
     */
    @PostRemove
    public void afterDelete(Todo todo) {
        AuditLog auditLog = AuditLog.builder()
                .entityName("Todo")
                .entityId(todo.getId())
                .action(AuditLog.AuditAction.DELETE)
                .oldValue(toJson(todo))
                .changedBy(getCurrentUser())
                .build();
        auditLogRepository.save(auditLog);
    }

    /**
     * Helper method to get current user (simplified for demo)
     * In production, use SecurityContextHolder or a request-scoped bean
     */
    private String getCurrentUser() {
        // TODO: Integrate with Spring Security to get actual user
        return "system";
    }

    /**
     * Helper method to convert entity to JSON string
     * In production, use a proper ObjectMapper
     */
    private String toJson(Object obj) {
        if (obj == null) return null;
        // Simplified JSON for demo - use ObjectMapper in production
        return String.format("{\"id\": %d, \"title\": \"%s\"}",
            obj instanceof Todo ? ((Todo) obj).getId() : 0,
            obj instanceof Todo ? ((Todo) obj).getTitle() : "");
    }
}
