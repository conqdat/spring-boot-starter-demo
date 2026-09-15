package com.learing.spring_boot_starter_demo.listener;

import com.learing.spring_boot_starter_demo.event.TodoCompletedEvent;
import com.learing.spring_boot_starter_demo.event.TodoCreatedEvent;
import com.learing.spring_boot_starter_demo.event.UserRegisteredEvent;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.model.User;
import com.learing.spring_boot_starter_demo.service.AuditLogService;
import com.learing.spring_boot_starter_demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final EmailService emailService;
    private final AuditLogService auditLogService;

    @EventListener
    public void handleTodoCreated(TodoCreatedEvent event) {
        Todo todo = event.getTodo();
        log.info("Todo created event received: {} (ID: {})", todo.getTitle(), todo.getId());

        // Send email notification if todo is assigned to a user
        if (todo.getAssignedUser() != null) {
            User assignedUser = todo.getAssignedUser();
            emailService.sendTodoAssignedEmail(
                assignedUser.getEmail(),
                assignedUser.getUsername(),
                todo.getTitle()
            );
        }
    }

    @EventListener
    public void handleTodoCompleted(TodoCompletedEvent event) {
        Todo todo = event.getTodo();
        log.info("Todo completed event received: {} (ID: {})", todo.getTitle(), todo.getId());

        // Log completion in audit
        auditLogService.logUpdate("Todo", todo.getId(),
            null, "{\"completed\": true}", event.getCompletedBy());
    }

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        log.info("User registered event received: {} (ID: {})", user.getUsername(), user.getId());

        // Send welcome email asynchronously
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
    }
}
