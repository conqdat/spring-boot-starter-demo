package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByCompleted(Boolean completed);

    List<Todo> findByTitleContainingIgnoreCase(String title);
}