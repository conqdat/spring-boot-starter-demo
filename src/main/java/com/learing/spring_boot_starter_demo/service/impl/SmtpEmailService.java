package com.learing.spring_boot_starter_demo.service.impl;

import com.learing.spring_boot_starter_demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to Todo App!");
        message.setText(String.format(
            "Hello %s,\n\nWelcome to Todo App! Your account has been created successfully.\n\nBest regards,\nTodo App Team",
            username
        ));
        sendEmail(message, "welcome", to);
    }

    @Override
    public void sendTodoAssignedEmail(String to, String username, String todoTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Todo Assigned: " + todoTitle);
        message.setText(String.format(
            "Hello %s,\n\nA new todo has been assigned to you: \"%s\"\n\nPlease check your dashboard for details.\n\nBest regards,\nTodo App Team",
            username, todoTitle
        ));
        sendEmail(message, "todo-assigned", to);
    }

    @Override
    public void sendTodoDueReminderEmail(String to, String username, String todoTitle, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reminder: Todo Due Today - " + todoTitle);
        message.setText(String.format(
            "Hello %s,\n\nThis is a reminder that your todo \"%s\" is due today (%s).\n\nPlease complete it as soon as possible.\n\nBest regards,\nTodo App Team",
            username, todoTitle, dueDate
        ));
        sendEmail(message, "due-reminder", to);
    }

    @Override
    public void sendTodoEscalatedEmail(String to, String username, String todoTitle, String newPriority) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Todo Escalated: " + todoTitle);
        message.setText(String.format(
            "Hello %s,\n\nYour overdue todo \"%s\" has been escalated to priority: %s.\n\nPlease address it immediately.\n\nBest regards,\nTodo App Team",
            username, todoTitle, newPriority
        ));
        sendEmail(message, "escalated", to);
    }

    private void sendEmail(SimpleMailMessage message, String type, String to) {
        try {
            message.setFrom("noreply@todoapp.com");
            mailSender.send(message);
            log.info("Email [{}] sent successfully to: {}", type, to);
        } catch (Exception e) {
            log.error("Failed to send email [{}] to: {}. Error: {}", type, to, e.getMessage());
            // Don't throw - email failure should not break business operations
        }
    }
}
