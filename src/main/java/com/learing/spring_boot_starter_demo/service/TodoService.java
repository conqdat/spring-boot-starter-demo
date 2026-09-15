package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.dto.TodoRequest;
import com.learing.spring_boot_starter_demo.dto.TodoResponse;
import com.learing.spring_boot_starter_demo.event.TodoCompletedEvent;
import com.learing.spring_boot_starter_demo.event.TodoCreatedEvent;
import com.learing.spring_boot_starter_demo.exception.BusinessException;
import com.learing.spring_boot_starter_demo.exception.ResourceNotFoundException;
import com.learing.spring_boot_starter_demo.model.Tag;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.model.User;
import com.learing.spring_boot_starter_demo.model.enums.Priority;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import com.learing.spring_boot_starter_demo.repository.UserRepository;
import com.learing.spring_boot_starter_demo.specification.TodoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TagService tagService;
    private final EmailService emailService;
    private final Clock clock;

    // ============ Constants for Business Rules ============

    private static final int MAX_PENDING_TODOS_PER_USER = 50;
    private static final int REOPEN_WINDOW_DAYS = 7;

    // ============ Basic CRUD Operations ============

    /**
     * Get all todos
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all todos with pagination and sorting
     */
    @Transactional(readOnly = true)
    public Page<TodoResponse> getAllTodosPaginated(Pageable pageable) {
        return todoRepository.findAll(pageable)
                .map(TodoResponse::fromEntity);
    }

    /**
     * Get todo by ID (cached)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "todos", key = "#id")
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        return TodoResponse.fromEntity(todo);
    }

    /**
     * Create a new todo with business rule validation
     */
    public TodoResponse createTodo(TodoRequest request) {
        // Business Rule 1: Cannot create todo with due date in the past
        validateDueDate(request.getDueDate());

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .dueDate(request.getDueDate())
                .priority(request.getPriority())
                .build();

        // Assign to user if provided
        if (request.getAssignedUserId() != null) {
            User user = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssignedUserId()));

            // Business Rule 3: Max 50 pending todos per user
            validatePendingTodoLimit(user.getId());

            todo.setAssignedUser(user);
        }

        // Handle tags
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<Tag> tags = tagService.findOrCreateTags(request.getTagNames());
            todo.setTags(tags);
        }

        Todo savedTodo = todoRepository.save(todo);

        // Publish event (triggers email notification if assigned)
        eventPublisher.publishEvent(new TodoCreatedEvent(this, savedTodo));

        return TodoResponse.fromEntity(savedTodo);
    }

    /**
     * Update an existing todo
     */
    @CacheEvict(value = "todos", key = "#id")
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        // Validate due date if being changed
        if (request.getDueDate() != null && !request.getDueDate().equals(todo.getDueDate())) {
            validateDueDate(request.getDueDate());
        }

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());

        // Business Rule 4 & 5: Handle completion/reopening logic
        if (request.getCompleted() != null) {
            handleCompletionChange(todo, request.getCompleted());
        }

        if (request.getDueDate() != null) {
            todo.setDueDate(request.getDueDate());
        }

        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }

        // Update assigned user
        if (request.getAssignedUserId() != null) {
            User user = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssignedUserId()));
            todo.setAssignedUser(user);
        } else if (request.getAssignedUserId() == null && todo.getAssignedUser() != null) {
            todo.setAssignedUser(null);
        }

        // Handle tags
        if (request.getTagNames() != null) {
            Set<Tag> tags = tagService.findOrCreateTags(request.getTagNames());
            todo.setTags(tags);
        }

        Todo updatedTodo = todoRepository.save(todo);
        return TodoResponse.fromEntity(updatedTodo);
    }

    /**
     * Delete a todo
     */
    @CacheEvict(value = "todos", key = "#id")
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }

    // ============ Business Rule Methods ============

    /**
     * Business Rule 1: Cannot create todo with due date in the past
     */
    private void validateDueDate(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now(clock))) {
            throw new BusinessException("Due date cannot be in the past");
        }
    }

    /**
     * Business Rule 3: Each user can have at most MAX_PENDING_TODOS_PER_USER uncompleted todos
     */
    private void validatePendingTodoLimit(Long userId) {
        long pendingCount = todoRepository.countPendingByUserId(userId);
        if (pendingCount >= MAX_PENDING_TODOS_PER_USER) {
            throw new BusinessException(
                String.format("User has reached the maximum limit of %d pending todos", MAX_PENDING_TODOS_PER_USER));
        }
    }

    /**
     * Business Rule 4 & 5: Handle completion state changes
     * - When marking as completed: set completedAt timestamp
     * - When reopening: check if completed more than REOPEN_WINDOW_DAYS days ago
     */
    private void handleCompletionChange(Todo todo, boolean newCompleted) {
        boolean wasCompleted = Boolean.TRUE.equals(todo.getCompleted());

        if (!wasCompleted && newCompleted) {
            // Marking as completed
            todo.setCompleted(true);
            todo.setCompletedAt(LocalDateTime.now(clock));
            eventPublisher.publishEvent(new TodoCompletedEvent(this, todo, "system"));
        } else if (wasCompleted && !newCompleted) {
            // Reopening — check if allowed
            if (todo.getCompletedAt() != null) {
                LocalDateTime reopenDeadline = todo.getCompletedAt().plusDays(REOPEN_WINDOW_DAYS);
                if (LocalDateTime.now(clock).isAfter(reopenDeadline)) {
                    throw new BusinessException(
                        String.format("Cannot reopen todo — it was completed more than %d days ago", REOPEN_WINDOW_DAYS));
                }
            }
            todo.setCompleted(false);
            todo.setCompletedAt(null);
        } else {
            todo.setCompleted(newCompleted);
        }
    }

    // ============ Filtering and Search Operations ============

    /**
     * Get todos by completed status
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByCompleted(Boolean completed) {
        return todoRepository.findByCompleted(completed).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get todos by completed status with pagination
     */
    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodosByCompletedPaginated(Boolean completed, Pageable pageable) {
        return todoRepository.findByCompleted(completed, pageable)
                .map(TodoResponse::fromEntity);
    }

    /**
     * Get todos by priority
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByPriority(Priority priority) {
        return todoRepository.findByPriority(priority).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get todos by due date
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByDueDate(LocalDate dueDate) {
        return todoRepository.findByDueDate(dueDate).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue todos
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getOverdueTodos() {
        return todoRepository.findOverdueTodos(LocalDate.now(clock)).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get todos due today
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosDueToday() {
        return todoRepository.findTodosDueToday(LocalDate.now(clock)).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming todos (due within next 7 days)
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getUpcomingTodos() {
        LocalDate today = LocalDate.now(clock);
        return todoRepository.findUpcomingTodos(today, today.plusDays(7)).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ============ User-Specific Operations ============

    /**
     * Get todos assigned to a specific user
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByUserId(Long userId) {
        return todoRepository.findByAssignedUserId(userId).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get todos assigned to a specific user with pagination
     */
    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodosByUserIdPaginated(Long userId, Pageable pageable) {
        return todoRepository.findByAssignedUserId(userId, pageable)
                .map(TodoResponse::fromEntity);
    }

    /**
     * Get completed todos for a user
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getCompletedTodosByUserId(Long userId) {
        return todoRepository.findByAssignedUserIdAndCompleted(userId, true).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get pending todos for a user
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getPendingTodosByUserId(Long userId) {
        return todoRepository.findByAssignedUserIdAndCompleted(userId, false).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ============ Specification-Based Dynamic Queries ============

    /**
     * Search todos using specifications (dynamic filtering)
     */
    @Transactional(readOnly = true)
    public Page<TodoResponse> searchTodos(Map<String, Object> filters, Pageable pageable) {
        Specification<Todo> spec = (Specification<Todo>) null;

        if (filters.containsKey("title") && filters.get("title") != null) {
            spec = spec.and(TodoSpecification.hasTitle(filters.get("title").toString()));
        }

        if (filters.containsKey("description") && filters.get("description") != null) {
            spec = spec.and(TodoSpecification.hasDescription(filters.get("description").toString()));
        }

        if (filters.containsKey("completed") && filters.get("completed") != null) {
            spec = spec.and(TodoSpecification.isCompleted((Boolean) filters.get("completed")));
        }

        if (filters.containsKey("priority") && filters.get("priority") != null) {
            spec = spec.and(TodoSpecification.hasPriority(filters.get("priority").toString()));
        }

        if (filters.containsKey("userId") && filters.get("userId") != null) {
            spec = spec.and(TodoSpecification.hasUserId((Long) filters.get("userId")));
        }

        if ("overdue".equals(filters.get("status"))) {
            spec = spec.and(TodoSpecification.isOverdue());
        } else if ("today".equals(filters.get("status"))) {
            spec = spec.and(TodoSpecification.isDueToday());
        } else if ("upcoming".equals(filters.get("status"))) {
            spec = spec.and(TodoSpecification.isUpcoming());
        }

        return todoRepository.findAll(spec, pageable)
                .map(TodoResponse::fromEntity);
    }

    /**
     * Search todos by title and description
     */
    @Transactional(readOnly = true)
    public Page<TodoResponse> searchByTitleOrDescription(String searchTerm, Pageable pageable) {
        Specification<Todo> spec = TodoSpecification.hasTitleAndDescription(searchTerm);
        return todoRepository.findAll(spec, pageable)
                .map(TodoResponse::fromEntity);
    }

    // ============ Bulk Operations ============

    /**
     * Mark all todos as completed for a user
     */
    @Transactional
    @CacheEvict(value = {"todos", "stats"}, allEntries = true)
    public int markAllTodosAsCompletedForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return todoRepository.markAllAsCompleted(userId);
    }

    /**
     * Delete all todos for a user
     */
    @Transactional
    @CacheEvict(value = {"todos", "stats"}, allEntries = true)
    public int deleteAllTodosForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return todoRepository.deleteAllByUserId(userId);
    }

    /**
     * Bulk update priority for a user's todos
     */
    @Transactional
    @CacheEvict(value = {"todos", "stats"}, allEntries = true)
    public int updatePriorityForUserTodos(Long userId, String priority) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return todoRepository.updatePriorityForUser(userId, priority);
    }

    // ============ Statistics and Analytics ============

    /**
     * Get todo statistics for a user
     */
    @Transactional(readOnly = true)
    public TodoStats getUserTodoStats(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        long total = todoRepository.countByUserId(userId);
        long completed = todoRepository.countCompletedByUserId(userId);
        long pending = todoRepository.countPendingByUserId(userId);
        long overdue = todoRepository.findOverdueTodos(LocalDate.now(clock)).stream()
                .filter(t -> t.getAssignedUser() != null && t.getAssignedUser().getId().equals(userId))
                .count();

        return TodoStats.builder()
                .userId(userId)
                .totalTodos(total)
                .completedTodos(completed)
                .pendingTodos(pending)
                .overdueTodos(overdue)
                .completionRate(total > 0 ? (completed * 100.0 / total) : 0.0)
                .build();
    }

    /**
     * Get global todo statistics (cached)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "stats", key = "'global'")
    public GlobalTodoStats getGlobalTodoStats() {
        long totalTodos = todoRepository.count();
        long completedTodos = todoRepository.findByCompleted(true).size();
        long pendingTodos = todoRepository.findByCompleted(false).size();
        long overdueTodos = todoRepository.findOverdueTodos(LocalDate.now(clock)).size();
        long dueToday = todoRepository.findTodosDueToday(LocalDate.now(clock)).size();

        return GlobalTodoStats.builder()
                .totalTodos(totalTodos)
                .completedTodos(completedTodos)
                .pendingTodos(pendingTodos)
                .overdueTodos(overdueTodos)
                .dueToday(dueToday)
                .completionRate(totalTodos > 0 ? (completedTodos * 100.0 / totalTodos) : 0.0)
                .build();
    }

    // ============ Helper Classes ============

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TodoStats {
        private Long userId;
        private Long totalTodos;
        private Long completedTodos;
        private Long pendingTodos;
        private Long overdueTodos;
        private Double completionRate;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class GlobalTodoStats {
        private Long totalTodos;
        private Long completedTodos;
        private Long pendingTodos;
        private Long overdueTodos;
        private Long dueToday;
        private Double completionRate;
    }
}
