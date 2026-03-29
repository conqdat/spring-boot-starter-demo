package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.dto.AuditSummaryDTO;
import com.learing.spring_boot_starter_demo.model.AuditLog;
import com.learing.spring_boot_starter_demo.model.AuditLog.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by entity name
     */
    List<AuditLog> findByEntityNameOrderByChangedAtDesc(String entityName);

    /**
     * Find audit logs by entity ID
     */
    List<AuditLog> findByEntityIdOrderByChangedAtDesc(Long entityId);

    /**
     * Find audit logs by action type
     */
    List<AuditLog> findByActionOrderByChangedAtDesc(AuditAction action);

    /**
     * Find audit logs by changed by user
     */
    Page<AuditLog> findByChangedByOrderByChangedAtDesc(String changedBy, Pageable pageable);

    /**
     * Find audit logs within a date range
     */
    List<AuditLog> findByChangedAtBetweenOrderByChangedAtDesc(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Find audit logs by entity name and action
     */
    List<AuditLog> findByEntityNameAndActionOrderByChangedAtDesc(
            String entityName,
            AuditAction action
    );

    /**
     * Count audit logs by entity name
     */
    long countByEntityName(String entityName);

    /**
     * Custom query to get recent audit activity summary
     */
    @Query("SELECT new com.learing.spring_boot_starter_demo.dto.AuditSummaryDTO(" +
           "a.entityName, a.action, COUNT(a)) " +
           "FROM AuditLog a " +
           "WHERE a.changedAt >= :since " +
           "GROUP BY a.entityName, a.action " +
           "ORDER BY COUNT(a) DESC")
    List<AuditSummaryDTO> getAuditSummary(@Param("since") LocalDateTime since);
}
