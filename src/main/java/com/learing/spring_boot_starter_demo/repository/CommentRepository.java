package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByTodoIdOrderByCreatedAtDesc(Long todoId, Pageable pageable);

    List<Comment> findByTodoIdOrderByCreatedAtDesc(Long todoId);

    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    long countByTodoId(Long todoId);

    void deleteAllByTodoId(Long todoId);
}
