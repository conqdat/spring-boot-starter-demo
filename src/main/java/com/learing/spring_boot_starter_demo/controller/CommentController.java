package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.dto.CommentRequest;
import com.learing.spring_boot_starter_demo.dto.CommentResponse;
import com.learing.spring_boot_starter_demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos/{todoId}/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long todoId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse comment = commentService.addComment(todoId, request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long todoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> comments = commentService.getCommentsByTodoId(todoId, pageable);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long todoId,
            @PathVariable Long commentId,
            @RequestParam Long requestingUserId) {
        commentService.deleteComment(todoId, commentId, requestingUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long todoId) {
        long count = commentService.getCommentCount(todoId);
        return ResponseEntity.ok(count);
    }
}
