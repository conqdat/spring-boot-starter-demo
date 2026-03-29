package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.dto.TodoRequest;
import com.learing.spring_boot_starter_demo.dto.TodoResponse;
import com.learing.spring_boot_starter_demo.exception.ResourceNotFoundException;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.model.User;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import com.learing.spring_boot_starter_demo.repository.UserRepository;
import com.learing.spring_boot_starter_demo.specification.TodoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

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
     * Get todo by ID
     */
    @Transactional(readOnly = true)
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        return TodoResponse.fromEntity(todo);
    }

    /**
     * Create a new todo
     */
    public TodoResponse createTodo(TodoRequest request) {
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
            todo.setAssignedUser(user);
        }

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.fromEntity(savedTodo);
    }

    /**
     * Update an existing todo
     */
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());

        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
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
            // If explicitly set to null, unassign the user
            todo.setAssignedUser(null);
        }

        Todo updatedTodo = todoRepository.save(todo);
        return TodoResponse.fromEntity(updatedTodo);
    }

    /**
     * Delete a todo
     */
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
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
    public List<TodoResponse> getTodosByPriority(String priority) {
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
        return todoRepository.findOverdueTodos(LocalDate.now()).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get todos due today
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosDueToday() {
        return todoRepository.findTodosDueToday(LocalDate.now()).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming todos (due within next 7 days)
     */
    @Transactional(readOnly = true)
    public List<TodoResponse> getUpcomingTodos() {
        return todoRepository.findUpcomingTodos(LocalDate.now(), LocalDate.now().plusDays(7)).stream()
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
     *
     * Example usage:
     * Map<String, Object> filters = Map.of(
     *     "title", "meeting",
     *     "completed", false,
     *     "priority", "HIGH"
     * );
     * Page<TodoResponse> results = todoService.searchTodos(filters, PageRequest.of(0, 10));
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
    public int markAllTodosAsCompletedForUser(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return todoRepository.markAllAsCompleted(userId);
    }

    /**
     * Delete all todos for a user
     */
    @Transactional
    public int deleteAllTodosForUser(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return todoRepository.deleteAllByUserId(userId);
    }

    /**
     * Bulk update priority for a user's todos
     */
    @Transactional
    public int updatePriorityForUserTodos(Long userId, String priority) {
        // Verify user exists
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
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        long total = todoRepository.countByUserId(userId);
        long completed = todoRepository.countCompletedByUserId(userId);
        long pending = todoRepository.countPendingByUserId(userId);
        long overdue = todoRepository.findOverdueTodos(LocalDate.now()).stream()
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
     * Get global todo statistics
     */
    @Transactional(readOnly = true)
    public GlobalTodoStats getGlobalTodoStats() {
        long totalTodos = todoRepository.count();
        long completedTodos = todoRepository.findByCompleted(true).size();
        long pendingTodos = todoRepository.findByCompleted(false).size();
        long overdueTodos = todoRepository.findOverdueTodos(LocalDate.now()).size();
        long dueToday = todoRepository.findTodosDueToday(LocalDate.now()).size();

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
