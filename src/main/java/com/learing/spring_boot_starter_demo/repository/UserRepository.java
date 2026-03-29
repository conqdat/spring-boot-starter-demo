package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (case-insensitive)
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if username exists (case-insensitive)
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Check if email exists (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Search users by username or email containing the search term
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);

    /**
     * Find users with pagination
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Find users ordered by todo count (users with most todos first)
     */
    @Query("SELECT u FROM User u LEFT JOIN u.todos t GROUP BY u.id ORDER BY COUNT(t) DESC")
    List<User> findUsersOrderedByTodoCount();
}
