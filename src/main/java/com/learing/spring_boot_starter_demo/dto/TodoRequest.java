package com.learing.spring_boot_starter_demo.dto;

import com.learing.spring_boot_starter_demo.validation.UniqueTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    @UniqueTitle(message = "A todo with this title already exists")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Boolean completed;
}