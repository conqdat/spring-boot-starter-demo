package com.learing.spring_boot_starter_demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learing.spring_boot_starter_demo.model.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {

    private Long id;
    private String content;
    private AuthorInfo author;
    private Long todoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(AuthorInfo.builder()
                    .id(comment.getAuthor().getId())
                    .username(comment.getAuthor().getUsername())
                    .build())
                .todoId(comment.getTodo().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorInfo {
        private Long id;
        private String username;
    }
}
