package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.dto.TodoRequest;
import com.learing.spring_boot_starter_demo.dto.TodoResponse;
import com.learing.spring_boot_starter_demo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    // ============ Basic CRUD Operations ============

    /**
     * GET /api/todos - Get all todos
     */
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/paginated - Get all todos with pagination
     * Query params: page (default 0), size (default 10), sortBy (default createdAt), direction (default DESC)
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<TodoResponse>> getAllTodosPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TodoResponse> todos = todoService.getAllTodosPaginated(pageable);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/{id} - Get todo by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    /**
     * POST /api/todos - Create new todo
     */
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @Valid @RequestBody TodoRequest request) {
        TodoResponse createdTodo = todoService.createTodo(request);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    /**
     * PUT /api/todos/{id} - Update existing todo
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request) {
        TodoResponse updatedTodo = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updatedTodo);
    }

    /**
     * DELETE /api/todos/{id} - Delete todo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ============ Filtering Operations ============

    /**
     * GET /api/todos/completed/{completed} - Filter by completed status
     */
    @GetMapping("/completed/{completed}")
    public ResponseEntity<List<TodoResponse>> getTodosByCompleted(
            @PathVariable Boolean completed) {
        List<TodoResponse> todos = todoService.getTodosByCompleted(completed);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/priority/{priority} - Filter by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TodoResponse>> getTodosByPriority(
            @PathVariable String priority) {
        List<TodoResponse> todos = todoService.getTodosByPriority(priority);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/due/{date} - Filter by due date (format: YYYY-MM-DD)
     */
    @GetMapping("/due/{date}")
    public ResponseEntity<List<TodoResponse>> getTodosByDueDate(
            @PathVariable LocalDate date) {
        List<TodoResponse> todos = todoService.getTodosByDueDate(date);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/overdue - Get overdue todos
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TodoResponse>> getOverdueTodos() {
        List<TodoResponse> todos = todoService.getOverdueTodos();
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/today - Get todos due today
     */
    @GetMapping("/today")
    public ResponseEntity<List<TodoResponse>> getTodosDueToday() {
        List<TodoResponse> todos = todoService.getTodosDueToday();
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/upcoming - Get upcoming todos (due within 7 days)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<TodoResponse>> getUpcomingTodos() {
        List<TodoResponse> todos = todoService.getUpcomingTodos();
        return ResponseEntity.ok(todos);
    }

    // ============ User-Specific Operations ============

    /**
     * GET /api/todos/user/{userId} - Get todos by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TodoResponse>> getTodosByUserId(
            @PathVariable Long userId) {
        List<TodoResponse> todos = todoService.getTodosByUserId(userId);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/user/{userId}/paginated - Get todos by user ID with pagination
     */
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<Page<TodoResponse>> getTodosByUserIdPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TodoResponse> todos = todoService.getTodosByUserIdPaginated(userId, pageable);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/user/{userId}/completed - Get completed todos for user
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<TodoResponse>> getCompletedTodosByUserId(
            @PathVariable Long userId) {
        List<TodoResponse> todos = todoService.getCompletedTodosByUserId(userId);
        return ResponseEntity.ok(todos);
    }

    /**
     * GET /api/todos/user/{userId}/pending - Get pending todos for user
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<TodoResponse>> getPendingTodosByUserId(
            @PathVariable Long userId) {
        List<TodoResponse> todos = todoService.getPendingTodosByUserId(userId);
        return ResponseEntity.ok(todos);
    }

    // ============ Search Operations ============

    /**
     * POST /api/todos/search - Search todos with dynamic filters
     * Request body: Map of filters (title, description, completed, priority, userId, status)
     * status can be: "overdue", "today", "upcoming"
     */
    @PostMapping("/search")
    public ResponseEntity<Page<TodoResponse>> searchTodos(
            @RequestBody Map<String, Object> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TodoResponse> results = todoService.searchTodos(filters, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/todos/search/text - Search by title or description
     */
    @GetMapping("/search/text")
    public ResponseEntity<Page<TodoResponse>> searchByTitleOrDescription(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TodoResponse> results = todoService.searchByTitleOrDescription(q, pageable);
        return ResponseEntity.ok(results);
    }

    // ============ Bulk Operations ============

    /**
     * POST /api/todos/user/{userId}/complete-all - Mark all todos as completed for user
     */
    @PostMapping("/user/{userId}/complete-all")
    public ResponseEntity<Integer> markAllAsCompleted(@PathVariable Long userId) {
        int count = todoService.markAllTodosAsCompletedForUser(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * DELETE /api/todos/user/{userId}/all - Delete all todos for user
     */
    @DeleteMapping("/user/{userId}/all")
    public ResponseEntity<Integer> deleteAllTodosForUser(@PathVariable Long userId) {
        int count = todoService.deleteAllTodosForUser(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * PUT /api/todos/user/{userId}/priority - Update priority for all user todos
     */
    @PutMapping("/user/{userId}/priority")
    public ResponseEntity<Integer> updatePriorityForUser(
            @PathVariable Long userId,
            @RequestParam String priority) {
        int count = todoService.updatePriorityForUserTodos(userId, priority);
        return ResponseEntity.ok(count);
    }

    // ============ Statistics ============

    /**
     * GET /api/todos/stats - Get global todo statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<TodoService.GlobalTodoStats> getGlobalStats() {
        TodoService.GlobalTodoStats stats = todoService.getGlobalTodoStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/todos/user/{userId}/stats - Get todo statistics for a user
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<TodoService.TodoStats> getUserStats(@PathVariable Long userId) {
        TodoService.TodoStats stats = todoService.getUserTodoStats(userId);
        return ResponseEntity.ok(stats);
    }
}
