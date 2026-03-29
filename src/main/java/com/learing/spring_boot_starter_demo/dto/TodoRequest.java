package com.learing.spring_boot_starter_demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Boolean completed;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Size(max = 50, message = "Priority cannot exceed 50 characters")
    private String priority;

    private Long assignedUserId;
}