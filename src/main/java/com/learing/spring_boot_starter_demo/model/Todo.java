package com.learing.spring_boot_starter_demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.learing.spring_boot_starter_demo.model.enums.Priority;

@Entity
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToMany
    @JoinTable(
        name = "todo_tags",
        joinColumns = @JoinColumn(name = "todo_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    @Setter
    private User assignedUser;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Check if this todo is overdue (past due date and not completed)
     */
    public boolean isOverdue() {
        return dueDate != null && !completed && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Check if this todo is due today
     */
    public boolean isDueToday() {
        return dueDate != null && LocalDate.now().isEqual(dueDate);
    }

    /**
     * Check if this todo is upcoming (due within next 7 days)
     */
    public boolean isUpcoming() {
        if (dueDate == null || completed) return false;
        return LocalDate.now().isBefore(dueDate) &&
               dueDate.isBefore(LocalDate.now().plusDays(7));
    }
}