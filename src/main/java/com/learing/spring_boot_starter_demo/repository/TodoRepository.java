package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

    // ============ Basic Query Methods ============

    /**
     * Find todos by completed status
     */
    List<Todo> findByCompleted(Boolean completed);

    /**
     * Find todos by title containing text (case-insensitive)
     */
    List<Todo> findByTitleContainingIgnoreCase(String title);

    // ============ Derived Query Methods ============

    /**
     * Find todos by priority
     */
    List<Todo> findByPriority(String priority);

    /**
     * Find todos by due date
     */
    List<Todo> findByDueDate(LocalDate dueDate);

    /**
     * Find todos that are overdue (past due date and not completed)
     */
    @Query("SELECT t FROM Todo t WHERE t.dueDate < :currentDate AND t.completed = false")
    List<Todo> findOverdueTodos(@Param("currentDate") LocalDate currentDate);

    /**
     * Find todos due today
     */
    @Query("SELECT t FROM Todo t WHERE t.dueDate = :today AND t.completed = false")
    List<Todo> findTodosDueToday(@Param("today") LocalDate today);

    /**
     * Find upcoming todos (due within next 7 days)
     */
    @Query("SELECT t FROM Todo t WHERE " +
           "t.dueDate > :today AND " +
           "t.dueDate <= :nextWeek AND " +
           "t.completed = false")
    List<Todo> findUpcomingTodos(
        @Param("today") LocalDate today,
        @Param("nextWeek") LocalDate nextWeek
    );

    /**
     * Find todos assigned to a specific user
     */
    List<Todo> findByAssignedUserId(Long userId);

    /**
     * Find todos assigned to a specific user with completed status
     */
    List<Todo> findByAssignedUserIdAndCompleted(Long userId, Boolean completed);

    // ============ Pagination Methods ============

    /**
     * Find todos by completed status with pagination
     */
    Page<Todo> findByCompleted(Boolean completed, Pageable pageable);

    /**
     * Find todos by priority with pagination
     */
    Page<Todo> findByPriority(String priority, Pageable pageable);

    /**
     * Find todos by user with pagination
     */
    Page<Todo> findByAssignedUserId(Long userId, Pageable pageable);

    /**
     * Find all todos with pagination and specification support
     */
    Page<Todo> findAll(Specification<Todo> spec, Pageable pageable);

    // ============ Aggregation Methods ============

    /**
     * Count todos by user
     */
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.assignedUser.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Count completed todos by user
     */
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.assignedUser.id = :userId AND t.completed = true")
    long countCompletedByUserId(@Param("userId") Long userId);

    /**
     * Count pending todos by user
     */
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.assignedUser.id = :userId AND t.completed = false")
    long countPendingByUserId(@Param("userId") Long userId);

    // ============ Bulk Operations ============

    /**
     * Mark all todos as completed for a user
     */
    @Modifying
    @Query("UPDATE Todo t SET t.completed = true WHERE t.assignedUser.id = :userId AND t.completed = false")
    int markAllAsCompleted(@Param("userId") Long userId);

    /**
     * Delete all todos assigned to a user
     */
    @Modifying
    @Query("DELETE FROM Todo t WHERE t.assignedUser.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Bulk update priority for todos matching criteria
     */
    @Modifying
    @Query("UPDATE Todo t SET t.priority = :priority WHERE t.assignedUser.id = :userId")
    int updatePriorityForUser(@Param("userId") Long userId, @Param("priority") String priority);
}