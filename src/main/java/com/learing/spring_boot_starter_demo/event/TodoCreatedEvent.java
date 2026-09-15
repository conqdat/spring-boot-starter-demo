package com.learing.spring_boot_starter_demo.event;

import com.learing.spring_boot_starter_demo.model.Todo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TodoCreatedEvent extends ApplicationEvent {

    private final Todo todo;

    public TodoCreatedEvent(Object source, Todo todo) {
        super(source);
        this.todo = todo;
    }
}
