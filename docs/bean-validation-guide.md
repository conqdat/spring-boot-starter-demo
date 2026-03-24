# Bean Validation with Custom Annotations in Spring Boot

A comprehensive guide from beginner to advanced level.

---

## Table of Contents

1. [Introduction](#introduction)
2. [Built-in Validation Annotations](#built-in-validation-annotations)
3. [Creating Custom Validation Annotations](#creating-custom-validation-annotations)
4. [Cross-Field Validation](#cross-field-validation)
5. [Validation Groups](#validation-groups)
6. [Method Validation](#method-validation)
7. [Best Practices](#best-practices)
8. [Real-World Examples](#real-world-examples)

---

## 1. Introduction

### What is Bean Validation?

Bean Validation (JSR 380 / Jakarta Validation) is a specification for validating Java beans through annotations. It allows you to declare validation rules directly on your model fields, making validation declarative and reusable.

### Why Use It?

- **Declarative**: Define rules with annotations, not imperative code
- **Reusable**: Write once, use everywhere
- **Standardized**: Part of Jakarta/Java specification
- **Integration**: Works seamlessly with Spring Boot
- **Customizable**: Create your own validation annotations

### Dependencies

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-validation'
}
```

### Quick Start Example

```java
@PostMapping("/todos")
public ResponseEntity<TodoResponse> createTodo(
    @Valid @RequestBody TodoRequest request) {
    // ...
}
```

When a request comes in, Spring automatically validates the `@RequestBody` against the constraints defined in `TodoRequest`.

---

## 2. Built-in Validation Annotations

### Common Annotations

| Annotation | Description | Example |
|------------|-------------|---------|
| `@NotNull` | Value must not be null | `@NotNull private String code;` |
| `@NotBlank` | String must not be null or empty (trimmed) | `@NotBlank private String title;` |
| `@NotEmpty` | Collection/String must not be empty | `@NotEmpty private List<String> tags;` |
| `@Size` | String/collection size constraints | `@Size(min=3, max=100)` |
| `@Min` / `@Max` | Numeric value range | `@Min(1) @Max(100)` |
| `@DecimalMin` / `@DecimalMax` | Decimal value range | `@DecimalMin("0.01")` |
| `@Email` | Valid email format | `@Email private String email;` |
| `@Pattern` | Regex pattern match | `@Pattern(regexp = "^[A-Z]{3}$")` |
| `@Positive` / `@Negative` | Positive/negative numbers | `@Positive private BigDecimal amount;` |
| `@Past` / `@Future` | Date in past or future | `@Past private LocalDate birthDate;` |
| `@URL` | Valid URL format | `@URL private String website;` |

### Complete Example: TodoRequest

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Boolean completed;

    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority cannot exceed 5")
    private Integer priority;

    @Email(message = "Invalid email format")
    private String assigneeEmail;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    @NotEmpty(message = "At least one tag is required")
    private List<String> tags;
}
```

### Controller Usage

```java
@PostMapping("/todos")
@ResponseStatus(HttpStatus.CREATED)
public TodoResponse createTodo(@Valid @RequestBody TodoRequest request) {
    // Validation happens automatically
    // If invalid, MethodArgumentNotValidException is thrown
    return todoService.createTodo(request);
}
```

### Default Exception Handling

Spring Boot returns a 400 Bad Request with validation errors:

```json
{
  "timestamp": "2026-03-24T10:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "One or more fields failed validation",
  "path": "/api/todos",
  "validationErrors": {
    "title": "Title is required",
    "assigneeEmail": "Invalid email format"
  }
}
```

---

## 3. Creating Custom Validation Annotations

### Why Create Custom Annotations?

1. **Reusability**: Use the same validation logic across multiple fields/classes
2. **Readability**: Business rules become self-documenting
3. **DRY**: Don't repeat validation logic
4. **Composability**: Combine multiple validations

### Step-by-Step: Creating a Custom Annotation

#### Step 1: Define the Annotation

```java
package com.learing.spring_boot_starter_demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTitleValidator.class)
public @interface UniqueTitle {

    String message() default "Title must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

**Key Elements:**
- `@Constraint(validatedBy = ...)`: Links to the validator class
- `message()`: Default error message
- `groups()`: For validation groups (covered later)
- `payload()`: For metadata (advanced)
- `@Target`: Where annotation can be used (FIELD, PARAMETER, etc.)
- `@Retention(RUNTIME)`: Must be runtime for reflection

#### Step 2: Implement the Validator

```java
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
        // Null values are handled by @NotNull - skip validation
        if (title == null || title.isBlank()) {
            return true;
        }

        // Check if title exists (case-insensitive)
        return !todoRepository.existsByTitleIgnoreCase(title.trim());
    }
}
```

**Important Notes:**
- Validator classes must be Spring components (`@Component`)
- `isValid()` returns `true` if valid, `false` if invalid
- Null values are typically handled by `@NotNull` separately
- You can inject other beans (repositories, services)

#### Step 3: Use the Annotation

```java
@Data
public class TodoRequest {

    @UniqueTitle(message = "A todo with this title already exists")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    // other fields...
}
```

---

## 4. Cross-Field Validation

### The Problem

Sometimes validation depends on multiple fields:

```java
public class TodoRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    // endDate must be after startDate
}
```

You can't put `@Future` on `endDate` alone - you need to compare two fields.

### Solution: Class-Level Constraint

#### Step 1: Create the Annotation

```java
@Target(ElementType.TYPE)  // Note: TYPE, not FIELD
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {

    String message() default "End date must be after start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String startField();
    String endField();
}
```

#### Step 2: Implement the Validator

```java
@Component
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            var startFieldObj = value.getClass()
                .getDeclaredField(startField)
                .get(value);

            var endFieldObj = value.getClass()
                .getDeclaredField(endField)
                .get(value);

            if (startFieldObj == null || endFieldObj == null) {
                return true;  // Let @NotNull handle nulls
            }

            var startDate = (LocalDateTime) startFieldObj;
            var endDate = (LocalDateTime) endFieldObj;

            return endDate.isAfter(startDate);

        } catch (Exception e) {
            throw new RuntimeException("Error validating date range", e);
        }
    }
}
```

#### Step 3: Apply to Class

```java
@Data
@DateRange(
    startField = "startDate",
    endField = "endDate",
    message = "End date must be after start date"
)
public class TodoRequest {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // other fields...
}
```

### Alternative: Explicit Fields (Cleaner)

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskDateValidator.class)
public @interface ValidTaskDates {
    String message() default "Invalid date configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class TaskDateValidator implements ConstraintValidator<ValidTaskDates, TodoRequest> {

    @Override
    public boolean isValid(TodoRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("End date cannot be before start date")
                .addPropertyNode("endDate")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
```

---

## 5. Validation Groups

### The Problem

Different operations need different validation rules:

```java
// CREATE: title required, id not needed
// UPDATE: id required, title optional
// PATCH: all fields optional
```

### Solution: Groups

#### Step 1: Define Group Interfaces

```java
public interface ValidationGroups {
    interface Create {}
    interface Update {}
    interface Patch {}
}
```

#### Step 2: Apply Groups to Fields

```java
@Data
public class TodoRequest {

    @NotNull(groups = ValidationGroups.Update.class, message = "ID required for update")
    private Long id;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Title required")
    @Size(min = 3, max = 100)
    private String title;

    @Size(max = 500, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;

    private Boolean completed;  // No validation - can be anything
}
```

#### Step 3: Use Groups in Controller

```java
@PostMapping("/todos")
@ResponseStatus(HttpStatus.CREATED)
public TodoResponse createTodo(
    @Validated(ValidationGroups.Create.class) @RequestBody TodoRequest request) {
    return todoService.createTodo(request);
}

@PutMapping("/todos/{id}")
public TodoResponse updateTodo(
    @PathVariable Long id,
    @Validated(ValidationGroups.Update.class) @RequestBody TodoRequest request) {
    return todoService.updateTodo(id, request);
}
```

### Default Group Behavior

When no group is specified, validation uses `Default.class`:

```java
// These are equivalent:
@Valid          // Uses Default group
@Validated      // Uses Default group
@Validated(Default.class)  // Explicit Default

// Custom group:
@Validated(ValidationGroups.Create.class)
```

---

## 6. Method Validation

### Validating Method Parameters

Spring can validate method parameters directly:

```java
@Service
@Validated  // Enable method validation
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoResponse createTodo(
        @Valid TodoRequest request,
        @NotBlank(message = "Title cannot be blank") String title) {
        // ...
    }
}
```

### Validating Return Values

```java
@Service
@Validated
public class TodoService {

    @NotNull(message = "Todo response must not be null")
    public TodoResponse getTodoById(@NotNull Long id) {
        // ...
    }
}
```

### Controller Method Validation

```java
@RestController
@RequestMapping("/api/todos")
@Validated  // Enable at controller level
public class TodoController {

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(
        @PathVariable @Min(1) Long id) {
        // ...
    }

    @GetMapping("/search")
    public ResponseEntity<List<TodoResponse>> searchTodos(
        @RequestParam @Size(min = 3) String query,
        @RequestParam(required = false) Integer page) {
        // ...
    }
}
```

### Enabling Method Validation Globally

```java
@Configuration
public class ValidationConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

---

## 7. Best Practices

### DO ✅

1. **Keep validation on DTOs**, not entities
   - Entities should assume valid input
   - DTOs are your API boundary

2. **Use meaningful error messages**
   ```java
   @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
   // NOT: "Invalid title"
   ```

3. **Combine annotations for clarity**
   ```java
   @NotBlank
   @Size(min = 3, max = 100)
   private String title;
   ```

4. **Use groups for different operations**
   ```java
   @NotNull(groups = Update.class)
   private Long id;
   ```

5. **Keep validators simple and focused**
   - One validation rule per validator class
   - Validators should be stateless when possible

### DON'T ❌

1. **Don't duplicate validation on entity and DTO**
   ```java
   // BAD: Duplicate
   @Entity
   public class Todo {
       @NotBlank  // ❌ Don't put here
       private String title;
   }

   @Data
   public class TodoRequest {
       @NotBlank  // ✅ Keep only here
       private String title;
   }
   ```

2. **Don't do database queries in validators when possible**
   - Makes testing harder
   - Can cause N+1 queries
   - Consider service-layer validation for complex checks

3. **Don't use validation for business logic**
   - Validation = structural checks
   - Business logic = domain rules
   ```java
   // Validation: @Min(1) priority
   // Business Logic: canUserAssignTodo(userId, todoId) - do in service
   ```

4. **Don't ignore null handling**
   - Most validators skip null values
   - Use `@NotNull` explicitly when needed

---

## 8. Real-World Examples

### Example 1: Phone Number Validation

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {
    String message() default "Invalid phone number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return true;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}

// Usage
@PhoneNumber
private String phone;
```

### Example 2: Strong Password Validation

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {
    String message() default "Password must contain uppercase, lowercase, digit, and special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int minLength() default 8;
}

@Component
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    private int minLength;

    @Override
    public void initialize(StrongPassword annotation) {
        this.minLength = annotation.minLength();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        if (password.length() < minLength) {
            return false;
        }

        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}

// Usage
@StrongPassword(minLength = 10)
private String password;
```

### Example 3: Business ID Validation (UUID Format)

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
public @interface ValidUuid {
    String message() default "Invalid UUID format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class UuidValidator implements ConstraintValidator<ValidUuid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            java.util.UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
```

### Example 4: Percentage Validation

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PercentageValidator.class)
public @interface Percentage {
    String message() default "Percentage must be between 0 and 100";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class PercentageValidator implements ConstraintValidator<Percentage, BigDecimal> {
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.compareTo(BigDecimal.ZERO) >= 0 &&
               value.compareTo(new BigDecimal("100")) <= 0;
    }
}
```

### Example 5: Content Type Validation (File Upload)

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContentTypeValidator.class)
public @interface ValidContentType {
    String message() default "Invalid file type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] allowedTypes() default {"image/jpeg", "image/png"};
}

@Component
public class ContentTypeValidator implements ConstraintValidator<ValidContentType, MultipartFile> {
    private String[] allowedTypes;

    @Override
    public void initialize(ValidContentType annotation) {
        this.allowedTypes = annotation.allowedTypes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.getContentType() == null) {
            return true;
        }

        return Arrays.stream(allowedTypes)
            .anyMatch(type -> type.equalsIgnoreCase(file.getContentType()));
    }
}

// Usage
@ValidContentType(allowedTypes = {"application/pdf", "application/msword"})
private MultipartFile document;
```

---

## Summary

| Topic | Key Takeaway |
|-------|--------------|
| Built-in annotations | Use `@NotBlank`, `@Size`, `@Email`, etc. for common validations |
| Custom annotations | Create reusable validation with `@Constraint` |
| Cross-field validation | Use class-level `@Target(ElementType.TYPE)` |
| Validation groups | Different rules for create/update/patch |
| Method validation | Add `@Validated` to service/controller |
| Best practices | Validate DTOs, not entities; keep validators simple |


---

## Resources

- [Jakarta Validation Specification](https://jakarta.ee/specifications/bean-validation/3.0/)
- [Spring Boot Validation Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.validation)
- [Hibernate Validator Guide](https://docs.jboss.org/hibernate/validator/8.0/reference/en-US/html/)
