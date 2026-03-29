package com.learing.spring_boot_starter_demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learing.spring_boot_starter_demo.model.Todo;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private LocalDate dueDate;
    private String priority;
    private UserInfo assignedUser;
    private Boolean isOverdue;
    private Boolean isDueToday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TodoResponse fromEntity(Todo todo) {
        TodoResponse.TodoResponseBuilder builder = TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.getCompleted())
                .dueDate(todo.getDueDate())
                .priority(todo.getPriority())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .isOverdue(todo.isOverdue())
                .isDueToday(todo.isDueToday());

        if (todo.getAssignedUser() != null) {
            builder.assignedUser(UserInfo.fromUser(todo.getAssignedUser()));
        }

        return builder.build();
    }

    /**
     * Nested class for representing user info in todo response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;

        public static UserInfo fromUser(com.learing.spring_boot_starter_demo.model.User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }
    }
}