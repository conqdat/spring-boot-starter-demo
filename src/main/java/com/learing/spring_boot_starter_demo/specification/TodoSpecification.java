package com.learing.spring_boot_starter_demo.specification;

import com.learing.spring_boot_starter_demo.model.Todo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * Specification class for building dynamic queries for Todo entities.
 *
 * Usage example:
 * <pre>
 * Specification&lt;Todo&gt; spec = Specification.where(hasTitle("meeting"))
 *     .and(hasPriority("HIGH"))
 *     .and(isCompleted(false))
 *     .and(hasDueDateBefore(LocalDate.now()));
 *
 * Page&lt;Todo&gt; results = todoRepository.findAll(spec, pageable);
 * </pre>
 */
public class TodoSpecification {

    /**
     * Filter todos by title containing the search term (case-insensitive)
     */
    public static Specification<Todo> hasTitle(String title) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(title)) {
                return null; // No filter applied if title is empty
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    /**
     * Filter todos by description containing the search term (case-insensitive)
     */
    public static Specification<Todo> hasDescription(String description) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(description)) {
                return null;
            }
            return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
        };
    }

    /**
     * Filter todos by completion status
     */
    public static Specification<Todo> isCompleted(Boolean completed) {
        return (root, query, cb) -> {
            if (completed == null) {
                return null;
            }
            return cb.equal(root.get("completed"), completed);
        };
    }

    /**
     * Filter todos by priority (case-insensitive exact match)
     */
    public static Specification<Todo> hasPriority(String priority) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(priority)) {
                return null;
            }
            return cb.equal(cb.lower(root.get("priority")), priority.toLowerCase());
        };
    }

    /**
     * Filter todos assigned to a specific user
     */
    public static Specification<Todo> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return null;
            }
            return cb.equal(root.get("assignedUser").get("id"), userId);
        };
    }

    /**
     * Filter todos with due date before the specified date
     */
    public static Specification<Todo> hasDueDateBefore(LocalDate dueDate) {
        return (root, query, cb) -> {
            if (dueDate == null) {
                return null;
            }
            return cb.lessThan(root.get("dueDate"), dueDate);
        };
    }

    /**
     * Filter todos with due date after the specified date
     */
    public static Specification<Todo> hasDueDateAfter(LocalDate dueDate) {
        return (root, query, cb) -> {
            if (dueDate == null) {
                return null;
            }
            return cb.greaterThan(root.get("dueDate"), dueDate);
        };
    }

    /**
     * Filter todos with due date on the specified date
     */
    public static Specification<Todo> hasDueDate(LocalDate dueDate) {
        return (root, query, cb) -> {
            if (dueDate == null) {
                return null;
            }
            return cb.equal(root.get("dueDate"), dueDate);
        };
    }

    /**
     * Filter todos that are overdue (due date before today and not completed)
     */
    public static Specification<Todo> isOverdue() {
        return (root, query, cb) -> {
            LocalDate today = LocalDate.now();
            return cb.and(
                cb.lessThan(root.get("dueDate"), today),
                cb.isFalse(root.get("completed"))
            );
        };
    }

    /**
     * Filter todos that are due today
     */
    public static Specification<Todo> isDueToday() {
        return (root, query, cb) -> {
            LocalDate today = LocalDate.now();
            return cb.and(
                cb.equal(root.get("dueDate"), today),
                cb.isFalse(root.get("completed"))
            );
        };
    }

    /**
     * Filter todos that are upcoming (due within next 7 days)
     */
    public static Specification<Todo> isUpcoming() {
        return (root, query, cb) -> {
            LocalDate today = LocalDate.now();
            LocalDate nextWeek = today.plusDays(7);
            return cb.and(
                cb.greaterThan(root.get("dueDate"), today),
                cb.lessThanOrEqualTo(root.get("dueDate"), nextWeek),
                cb.isFalse(root.get("completed"))
            );
        };
    }

    /**
     * Filter todos created between two dates
     */
    public static Specification<Todo> createdBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) {
                return null;
            }
            return cb.between(
                root.get("createdAt"),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
            );
        };
    }

    /**
     * Combine multiple specifications with AND logic
     */
    public static Specification<Todo> hasTitleAndDescription(String searchTerm) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return null;
            }
            String lowerSearchTerm = searchTerm.toLowerCase();
            return cb.or(
                cb.like(cb.lower(root.get("title")), "%" + lowerSearchTerm + "%"),
                cb.like(cb.lower(root.get("description")), "%" + lowerSearchTerm + "%")
            );
        };
    }
}
