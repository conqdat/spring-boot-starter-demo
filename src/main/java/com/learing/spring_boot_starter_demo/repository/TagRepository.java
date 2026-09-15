package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Tag> findByNameInIgnoreCase(Set<String> names);

    List<Tag> findByTodosId(Long todoId);
}
