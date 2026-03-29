package com.learing.spring_boot_starter_demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Audit log entity for tracking all changes to Todo entities.
 * This is a common pattern in enterprise applications for compliance and debugging.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(length = 2000)
    private String oldValue;

    @Column(length = 2000)
    private String newValue;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changedAt;

    /**
     * Enum representing the type of audit action
     */
    public enum AuditAction {
        CREATE,
        UPDATE,
        DELETE,
        BULK_UPDATE,
        BULK_DELETE
    }
}
