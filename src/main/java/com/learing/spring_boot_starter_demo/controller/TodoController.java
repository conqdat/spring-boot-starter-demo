package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.dto.TodoRequest;
import com.learing.spring_boot_starter_demo.dto.TodoResponse;
import com.learing.spring_boot_starter_demo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure appropriately for production
public class TodoController {

    private final TodoService todoService;

    /**
     * GET /api/todos - Get all todos
     */
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
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
     * GET /api/todos/completed/{completed} - Filter by completed status
     */
    @GetMapping("/completed/{completed}")
    public ResponseEntity<List<TodoResponse>> getTodosByCompleted(
            @PathVariable Boolean completed) {
        List<TodoResponse> todos = todoService.getTodosByCompleted(completed);
        return ResponseEntity.ok(todos);
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
}