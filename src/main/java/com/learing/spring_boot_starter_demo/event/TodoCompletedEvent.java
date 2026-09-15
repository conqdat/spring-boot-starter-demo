package com.learing.spring_boot_starter_demo.event;

import com.learing.spring_boot_starter_demo.model.Todo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TodoCompletedEvent extends ApplicationEvent {

    private final Todo todo;
    private final String completedBy;

    public TodoCompletedEvent(Object source, Todo todo, String completedBy) {
        super(source);
        this.todo = todo;
        this.completedBy = completedBy;
    }
}
