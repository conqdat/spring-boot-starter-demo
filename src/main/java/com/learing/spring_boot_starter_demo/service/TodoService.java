package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.dto.TodoRequest;
import com.learing.spring_boot_starter_demo.dto.TodoResponse;
import com.learing.spring_boot_starter_demo.exception.ResourceNotFoundException;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        return TodoResponse.fromEntity(todo);
    }

    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .build();

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.fromEntity(savedTodo);
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }

        Todo updatedTodo = todoRepository.save(todo);
        return TodoResponse.fromEntity(updatedTodo);
    }

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
    }

    public List<TodoResponse> getTodosByCompleted(Boolean completed) {
        return todoRepository.findByCompleted(completed).stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }
}