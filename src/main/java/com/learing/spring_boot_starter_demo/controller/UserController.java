package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.dto.UserRequest;
import com.learing.spring_boot_starter_demo.dto.UserResponse;
import com.learing.spring_boot_starter_demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // ============ Basic CRUD Operations ============

    /**
     * GET /api/users/paginated - Get all users with pagination
     * Query params: page (default 0), size (default 10), sortBy (default username), direction (default ASC)
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id} - Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/username/{username} - Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/users - Create new user
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * PUT /api/users/{id} - Update existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/users/{id} - Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ============ Search Operations ============

    /**
     * GET /api/users/search - Search users by username or email
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam String q) {
        List<UserResponse> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/by-todo-count - Get users ordered by todo count
     */
    @GetMapping("/by-todo-count")
    public ResponseEntity<List<UserResponse>> getUsersOrderedByTodoCount() {
        List<UserResponse> users = userService.getUsersOrderedByTodoCount();
        return ResponseEntity.ok(users);
    }

    // ============ Statistics ============

    /**
     * GET /api/users/stats - Get user statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<UserService.UserStats> getUserStats() {
        UserService.UserStats stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }
}
