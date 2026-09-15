package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.dto.TagRequest;
import com.learing.spring_boot_starter_demo.dto.TagResponse;
import com.learing.spring_boot_starter_demo.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        TagResponse tag = tagService.createTag(request);
        return new ResponseEntity<>(tag, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/todos/{todoId}/tags/{tagId}")
    public ResponseEntity<TagResponse> addTagToTodo(
            @PathVariable Long todoId,
            @PathVariable Long tagId) {
        TagResponse tag = tagService.addTagToTodo(todoId, tagId);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/todos/{todoId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromTodo(
            @PathVariable Long todoId,
            @PathVariable Long tagId) {
        tagService.removeTagFromTodo(todoId, tagId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<List<TagResponse>> getTagsByTodoId(@PathVariable Long todoId) {
        return ResponseEntity.ok(tagService.getTagsByTodoId(todoId));
    }
}
