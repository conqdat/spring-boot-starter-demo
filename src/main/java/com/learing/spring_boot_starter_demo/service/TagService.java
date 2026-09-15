package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.dto.TagRequest;
import com.learing.spring_boot_starter_demo.dto.TagResponse;
import com.learing.spring_boot_starter_demo.exception.DuplicateResourceException;
import com.learing.spring_boot_starter_demo.exception.ResourceNotFoundException;
import com.learing.spring_boot_starter_demo.model.Tag;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.repository.TagRepository;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(TagResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        return TagResponse.fromEntity(tag);
    }

    public TagResponse createTag(TagRequest request) {
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Tag already exists with name: " + request.getName());
        }

        Tag tag = Tag.builder()
                .name(request.getName())
                .color(request.getColor())
                .build();

        Tag saved = tagRepository.save(tag);
        return TagResponse.fromEntityWithoutCount(saved);
    }

    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }

    public TagResponse addTagToTodo(Long todoId, Long tagId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        todo.getTags().add(tag);
        todoRepository.save(todo);
        return TagResponse.fromEntity(tag);
    }

    public void removeTagFromTodo(Long todoId, Long tagId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        todo.getTags().remove(tag);
        todoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByTodoId(Long todoId) {
        return tagRepository.findByTodosId(todoId).stream()
                .map(TagResponse::fromEntityWithoutCount)
                .collect(Collectors.toList());
    }

    /**
     * Find or create tags by names — used when creating/updating todos with tag names
     */
    public Set<Tag> findOrCreateTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Set.of();
        }

        return tagNames.stream().map(name -> {
            return tagRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> tagRepository.save(
                    Tag.builder().name(name.trim()).build()
                ));
        }).collect(Collectors.toSet());
    }
}
