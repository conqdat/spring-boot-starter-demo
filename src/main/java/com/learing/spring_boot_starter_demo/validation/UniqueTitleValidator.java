package com.learing.spring_boot_starter_demo.validation;

import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueTitleValidator implements ConstraintValidator<UniqueTitle, String> {

    private final TodoRepository todoRepository;

    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        if (title == null || title.isBlank()) {
            return true;
        }

        return !todoRepository.existsByTitleIgnoreCase(title.trim());
    }
}