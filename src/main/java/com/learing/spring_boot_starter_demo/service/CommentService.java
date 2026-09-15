package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.dto.CommentRequest;
import com.learing.spring_boot_starter_demo.dto.CommentResponse;
import com.learing.spring_boot_starter_demo.exception.BusinessException;
import com.learing.spring_boot_starter_demo.exception.ResourceNotFoundException;
import com.learing.spring_boot_starter_demo.model.Comment;
import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.model.User;
import com.learing.spring_boot_starter_demo.repository.CommentRepository;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import com.learing.spring_boot_starter_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    /**
     * Add a comment to a todo
     */
    public CommentResponse addComment(Long todoId, CommentRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + todoId));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAuthorId()));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .todo(todo)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved);
    }

    /**
     * Get comments for a todo with pagination
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTodoId(Long todoId, Pageable pageable) {
        if (!todoRepository.existsById(todoId)) {
            throw new ResourceNotFoundException("Todo not found with id: " + todoId);
        }
        return commentRepository.findByTodoIdOrderByCreatedAtDesc(todoId, pageable)
                .map(CommentResponse::fromEntity);
    }

    /**
     * Delete a comment — only the author can delete their own comment
     */
    public void deleteComment(Long todoId, Long commentId, Long requestingUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getTodo().getId().equals(todoId)) {
            throw new BusinessException("Comment does not belong to the specified todo");
        }

        if (!comment.getAuthor().getId().equals(requestingUserId)) {
            throw new BusinessException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    /**
     * Get comment count for a todo
     */
    @Transactional(readOnly = true)
    public long getCommentCount(Long todoId) {
        return commentRepository.countByTodoId(todoId);
    }
}
