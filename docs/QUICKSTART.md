# ⚡ Quick Start Guide

Get your Spring Boot learning journey started in 10 minutes.

---

## Step 1: Verify Prerequisites

```bash
# Check Java version (need Java 17+)
java -version

# Check Gradle
./gradlew --version

# Check if project compiles
./gradlew build
```

---

## Step 2: Run the Starter Project

```bash
# Run with Gradle
./gradlew bootRun

# Or run from IDE
# Right-click Application.java → Run
```

Visit: http://localhost:8080

---

## Step 3: Your First Feature

Let's add a simple "Hello World" endpoint:

### 1. Create Controller

`src/main/java/com/learing/spring_boot_starter_demo/controller/HelloController.java`

```java
package com.learing.spring_boot_starter_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

### 2. Test It

```bash
curl http://localhost:8080/hello
# Expected: Hello, Spring Boot!
```

---

## Step 4: Add a Database Entity

### 1. Add Dependencies (build.gradle)

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
}
```

### 2. Create Entity

`src/main/java/com/learing/spring_boot_starter_demo/model/Todo.java`

```java
package com.learing.spring_boot_starter_demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed;
}
```

### 3. Create Repository

`src/main/java/com/learing/spring_boot_starter_demo/repository/TodoRepository.java`

```java
package com.learing.spring_boot_starter_demo.repository;

import com.learing.spring_boot_starter_demo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCompleted(boolean completed);
}
```

### 4. Create Service

`src/main/java/com/learing/spring_boot_starter_demo/service/TodoService.java`

```java
package com.learing.spring_boot_starter_demo.service;

import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo createTodo(String title) {
        Todo todo = Todo.builder()
                .title(title)
                .completed(false)
                .build();
        return todoRepository.save(todo);
    }

    public Todo toggleTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }
}
```

### 5. Create Controller

`src/main/java/com/learing/spring_boot_starter_demo/controller/TodoController.java`

```java
package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.model.Todo;
import com.learing.spring_boot_starter_demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }

    @PostMapping
    public Todo createTodo(@RequestBody String title) {
        return todoService.createTodo(title);
    }

    @PatchMapping("/{id}")
    public Todo toggleTodo(@PathVariable Long id) {
        return todoService.toggleTodo(id);
    }
}
```

### 6. Test It

```bash
# Get all todos
curl http://localhost:8080/api/todos

# Create todo
curl -X POST -H "Content-Type: application/json" \
  -d "Buy milk" http://localhost:8080/api/todos

# Toggle todo
curl -X PATCH http://localhost:8080/api/todos/1
```

---

## Step 5: Add a Test

`src/test/java/com/learing/spring_boot_starter_demo/controller/TodoControllerTest.java`

```java
package com.learing.spring_boot_starter_demo.controller;

import com.learing.spring_boot_starter_demo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void shouldCreateTodo() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType("application/json")
                .content("Test todo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test todo"));
    }
}
```

Run tests:
```bash
./gradlew test
```

---

## Step 6: Study Open Source Projects

Now that you've built a simple CRUD app, let's study real projects:

### Week 1: PetClinic
```bash
# Clone
git clone https://github.com/spring-projects/spring-petclinic
cd spring-petclinic

# Run
./mvnw spring-boot:run

# Study these files:
# - src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java
# - src/main/java/org/springframework/samples/petclinic/owner/Pet.java
# - src/main/resources/application.properties
```

### Week 2: RealWorld
```bash
git clone https://github.com/gothinkster/spring-boot-realworld-example-app
cd spring-boot-realworld-example-app

# Study:
# - How JWT auth is implemented
# - How DTOs are structured
# - How exceptions are handled
```

---

## Step 7: Daily Learning Routine

```
Morning (30 min):
  - Read Spring Boot docs or blog
  - Watch Spring Developer YouTube

Afternoon (1 hour):
  - Code your practice project
  - Follow tutorials

Evening (30 min):
  - Study open source code
  - Write learning journal
```

---

## 📚 Essential Resources

| Resource | Link |
|----------|------|
| Spring Boot Docs | https://docs.spring.io/spring-boot/ |
| Spring Guides | https://spring.io/guides |
| Spring YouTube | https://www.youtube.com/c/SpringDeveloper |
| Baeldung | https://www.baeldung.com/ |
| Spring Initializr | https://start.spring.io/ |

---

## ✅ Checklist for Day 1

- [ ] Java 17+ installed
- [ ] Project runs locally
- [ ] Hello endpoint works
- [ ] Todo CRUD works
- [ ] Test passes
- [ ] Cloned PetClinic
- [ ] Scheduled study time

---

**You're ready!** Start with PetClinic and follow the roadmap. 🚀
