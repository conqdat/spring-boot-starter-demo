package com.learing.spring_boot_starter_demo.service;

public interface EmailService {

    /**
     * Send welcome email when a new user registers
     */
    void sendWelcomeEmail(String to, String username);

    /**
     * Send notification when a todo is assigned to a user
     */
    void sendTodoAssignedEmail(String to, String username, String todoTitle);

    /**
     * Send reminder for todos that are due today
     */
    void sendTodoDueReminderEmail(String to, String username, String todoTitle, String dueDate);

    /**
     * Send notification when a todo is overdue and escalated
     */
    void sendTodoEscalatedEmail(String to, String username, String todoTitle, String newPriority);
}
