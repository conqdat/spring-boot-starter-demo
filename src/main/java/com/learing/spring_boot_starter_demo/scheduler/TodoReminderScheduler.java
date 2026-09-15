package com.learing.spring_boot_starter_demo.scheduler;

import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.model.User;
import com.learing.spring_boot_starter_demo.model.enums.Priority;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import com.learing.spring_boot_starter_demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TodoReminderScheduler {

    private final TodoRepository todoRepository;
    private final EmailService emailService;
    private final Clock clock;

    /**
     * Send reminders for todos due today — runs every day at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional(readOnly = true)
    public void sendDueTodayReminders() {
        LocalDate today = LocalDate.now(clock);
        List<Todo> todosDueToday = todoRepository.findTodosDueToday(today);

        log.info("Found {} todos due today", todosDueToday.size());

        for (Todo todo : todosDueToday) {
            if (todo.getAssignedUser() != null) {
                User user = todo.getAssignedUser();
                emailService.sendTodoDueReminderEmail(
                    user.getEmail(),
                    user.getUsername(),
                    todo.getTitle(),
                    today.toString()
                );
            }
        }
    }

    /**
     * Auto-escalate overdue URGENT todos — runs every hour
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void escalateOverdueTodos() {
        LocalDate today = LocalDate.now(clock);
        List<Todo> overdueTodos = todoRepository.findOverdueTodos(today);

        int escalatedCount = 0;
        for (Todo todo : overdueTodos) {
            if (todo.getPriority() != null && todo.getPriority() != Priority.URGENT) {
                LocalDate dueDate = todo.getDueDate();
                long daysOverdue = today.toEpochDay() - dueDate.toEpochDay();

                if (daysOverdue > 3) {
                    Priority oldPriority = todo.getPriority();
                    Priority newPriority = oldPriority.getEscalationTarget();
                    todo.setPriority(newPriority);
                    todoRepository.save(todo);
                    escalatedCount++;

                    log.info("Escalated todo '{}' from {} to {}",
                        todo.getTitle(), oldPriority, newPriority);

                    // Notify assigned user
                    if (todo.getAssignedUser() != null) {
                        emailService.sendTodoEscalatedEmail(
                            todo.getAssignedUser().getEmail(),
                            todo.getAssignedUser().getUsername(),
                            todo.getTitle(),
                            newPriority.name()
                        );
                    }
                }
            }
        }

        if (escalatedCount > 0) {
            log.info("Escalated {} overdue todos", escalatedCount);
        }
    }
}
