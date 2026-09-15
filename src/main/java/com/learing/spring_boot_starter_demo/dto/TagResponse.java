package com.learing.spring_boot_starter_demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learing.spring_boot_starter_demo.model.Tag;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagResponse {

    private Long id;
    private String name;
    private String color;
    private Long todoCount;
    private LocalDateTime createdAt;

    public static TagResponse fromEntity(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .todoCount((long) tag.getTodos().size())
                .createdAt(tag.getCreatedAt())
                .build();
    }

    public static TagResponse fromEntityWithoutCount(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .createdAt(tag.getCreatedAt())
                .build();
    }
}
