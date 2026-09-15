package com.learing.spring_boot_starter_demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.model.enums.Priority;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private Priority priority;
    private UserInfo assignedUser;
    private Boolean isOverdue;
    private Boolean isDueToday;
    private LocalDateTime completedAt;
    private List<TagInfo> tags;
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
                .isDueToday(todo.isDueToday())
                .completedAt(todo.getCompletedAt());

        if (todo.getAssignedUser() != null) {
            builder.assignedUser(UserInfo.fromUser(todo.getAssignedUser()));
        }

        if (todo.getTags() != null && !todo.getTags().isEmpty()) {
            builder.tags(todo.getTags().stream()
                    .map(tag -> TagInfo.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .color(tag.getColor())
                            .build())
                    .collect(Collectors.toList()));
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

    /**
     * Nested class for representing tag info in todo response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagInfo {
        private Long id;
        private String name;
        private String color;
    }
}