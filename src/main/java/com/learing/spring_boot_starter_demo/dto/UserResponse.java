package com.learing.spring_boot_starter_demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learing.spring_boot_starter_demo.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private TodoStats todoStats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .todoStats(TodoStats.fromUser(user))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserResponse fromUserWithoutTodos(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Nested class for representing todo statistics for a user
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TodoStats {
        private Long totalTodos;
        private Long completedTodos;
        private Long pendingTodos;
        private Long overdueTodos;

        public static TodoStats fromUser(User user) {
            if (user.getTodos() == null) {
                return null;
            }

            List<com.learing.spring_boot_starter_demo.model.Todo> todos = user.getTodos();
            return TodoStats.builder()
                    .totalTodos((long) todos.size())
                    .completedTodos(todos.stream().filter(com.learing.spring_boot_starter_demo.model.Todo::getCompleted).count())
                    .pendingTodos(todos.stream().filter(todo -> !todo.getCompleted()).count())
                    .overdueTodos(todos.stream().filter(com.learing.spring_boot_starter_demo.model.Todo::isOverdue).count())
                    .build();
        }
    }
}
