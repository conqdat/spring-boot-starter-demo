package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.dto.UserRequest;
import com.learing.spring_boot_starter_demo.dto.UserResponse;
import com.learing.spring_boot_starter_demo.exception.ResourceNotFoundException;
import com.learing.spring_boot_starter_demo.model.User;
import com.learing.spring_boot_starter_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::fromUserWithoutTodos);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromUser(user);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return UserResponse.fromUser(user);
    }

    /**
     * Create a new user
     */
    public UserResponse createUser(UserRequest request) {
        // Check for duplicate username
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Check for duplicate email
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .bio(request.getBio())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.fromUserWithoutTodos(savedUser);
    }

    /**
     * Update an existing user
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check for duplicate username (excluding current user)
        if (request.getUsername() != null &&
            !request.getUsername().equalsIgnoreCase(user.getUsername()) &&
            userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Check for duplicate email (excluding current user)
        if (request.getEmail() != null &&
            !request.getEmail().equalsIgnoreCase(user.getEmail()) &&
            userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    /**
     * Delete a user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Search users by username or email
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String searchTerm) {
        List<User> users = userRepository.searchUsers(searchTerm);
        return users.stream()
                .map(UserResponse::fromUserWithoutTodos)
                .collect(Collectors.toList());
    }

    /**
     * Get users ordered by todo count (users with most todos first)
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersOrderedByTodoCount() {
        List<User> users = userRepository.findUsersOrderedByTodoCount();
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        return UserStats.builder()
                .totalUsers(totalUsers)
                .build();
    }

    /**
     * Nested class for user statistics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserStats {
        private Long totalUsers;
    }
}
