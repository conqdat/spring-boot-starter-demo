# 📖 Spring Boot Study Guide

## How to Study Open Source Projects

### Step 1: Clone and Run

```bash
git clone <project-url>
cd <project>
./mvnw spring-boot:run   # or ./gradlew bootRun
```

**Goal:** Get it running locally first. Don't dive into code yet.

---

### Step 2: Trace the Request Flow

Pick one endpoint (e.g., `GET /api/articles`) and trace:

```
Controller → Service → Repository → Database
     ↓           ↓          ↓           ↓
  DTO       Business    Query      Entity
  mapping   logic       method
```

**Questions to ask:**
- How is the request validated?
- Where is business logic placed?
- How are exceptions handled?
- How is the response formatted?

---

### Step 3: Study Key Files

For each project, study these files in order:

| File Type | What to Look For | PetClinic Example |
|-----------|------------------|-------------------|
| Application.java | @SpringBootApplication config | PetClinicApplication.java |
| Controller | Request mapping, validation | OwnerController.java |
| Service | Business logic patterns | OwnerService.java |
| Repository | Query methods, @Query | OwnerRepository.java |
| Entity | JPA mappings, relationships | Owner.java, Pet.java |
| Exception | Error handling strategy | ResourceNotFoundException.java |
| Test | Testing patterns | OwnerControllerTest.java |

---

### Step 4: Code Along

After studying a pattern, implement it in your practice project:

```
Study PetClinic's OwnerController
    ↓
Implement TodoController with same patterns
    ↓
Compare your code with theirs
    ↓
Refactor based on learnings
```

---

## Project-Specific Study Guides

### PetClinic (Week 1-2)

**Focus:** Monolithic CRUD, JPA, Thymeleaf

**Files to study:**
```
src/main/java/org/springframework/samples/petclinic/
├── model/
│   ├── Person.java        # @MappedSuperclass pattern
│   ├── Owner.java         # Entity with relationships
│   └── Pet.java           # Entity with @OneToMany
├── owner/
│   ├── OwnerController.java    # MVC pattern
│   └── OwnerService.java       # Service layer
├── visit/
│   └── VisitController.java    # Nested resource pattern
└── system/
    └── CrashController.java    # Exception demo
```

**Key learnings:**
- `@MappedSuperclass` for DRY entity code
- `@OneToMany` with `@JoinColumn`
- `BeanPropertyBindingResult` for validation
- Thymeleaf fragment templates

**Exercise:** Add a `PetVaccination` entity with relationships

---

### RealWorld (Week 3-4)

**Focus:** REST API, JWT, Clean Architecture

**Files to study:**
```
src/main/java/io/realworld/
├── article/
│   ├── ArticleController.java    # REST controller
│   ├── ArticleService.java       # Service with JWT
│   └── ArticleRepository.java    # Custom repository
├── user/
│   ├── UserController.java       # Auth endpoints
│   └── UserService.java          # Password hashing
└── security/
    └── JwtTokenProvider.java     # JWT utility
```

**Key learnings:**
- DTO pattern for request/response
- JWT token generation and validation
- Repository custom implementations
- Exception handling with @ControllerAdvice

**Exercise:** Add article bookmarking feature

---

### Mall (Week 5-7)

**Focus:** E-commerce, Security, Redis, Elasticsearch

**Files to study:**
```
mall-admin/
├── controller/
│   ├── PmsProductController.java     # Product management
│   └── UmsMemberController.java      # User management
├── service/
│   ├── ProductService.java           # Cache integration
│   └── MemberService.java            # Business logic
├── dao/
│   └── ProductDao.java               # MyBatis mapper
└── domain/
    ├── Product.java                  # Entity
    └── Member.java                   # User entity
```

**Key learnings:**
- MyBatis vs JPA trade-offs
- Redis cache annotations (@Cacheable, @CacheEvict)
- Spring Security with JWT
- Swagger/OpenAPI documentation

**Exercise:** Add product search with Elasticsearch

---

### PiggyMetrics (Week 10-12)

**Focus:** Microservices, Spring Cloud

**Files to study:**
```
account-service/
├── controller/
│   └── AccountController.java    # Service endpoint
├── service/
│   └── AccountService.java       # Business logic
└── config/
    └── SecurityConfig.java       # OAuth2 config

gateway/
├── routes/                 # Route configuration
└── filters/                # Request filters

registry/
└── Eureka server config    # Service discovery
```

**Key learnings:**
- Service decomposition boundaries
- Inter-service communication (Feign)
- API Gateway pattern
- Distributed configuration

**Exercise:** Add a new statistics service

---

### Metasfresh (Week 17-20)

**Focus:** DDD, ERP, Complex Business Logic

**Files to study:**
```
services/
├── de.metas.invoice/           # Invoice domain
│   ├── Invoice.java            # Aggregate root
│   ├── InvoiceRepository.java  # Repository interface
│   └── InvoiceService.java     # Domain service
├── de.metas.order/             # Order domain
└── de.metas.product/           # Product domain
```

**Key learnings:**
- Aggregate root identification
- Value objects vs entities
- Domain events
- Repository pattern with DDD

**Exercise:** Model a new domain aggregate

---

## Daily Study Routine

### Morning (30 min)
- Read Spring documentation or blog
- Watch Spring Developer YouTube video
- Review yesterday's learnings

### Afternoon (1 hour)
- Code practice project
- Implement patterns from studied project
- Write tests

### Evening (30 min)
- Study open source codebase
- Take notes on patterns
- Update learning journal

---

## Note-Taking Template

```markdown
## Date: YYYY-MM-DD
## Project: [Project Name]

### What I Studied
- File: path/to/file.java
- Pattern: [Pattern name]

### Key Learnings
1. [Learning point 1]
2. [Learning point 2]

### Code Snippet
```java
// Paste interesting code here
```

### Questions
- [Question 1]
- [Question 2]

### Application
How will I use this in my practice project?
[Answer]
```

---

## Common Patterns to Recognize

### 1. Layered Architecture
```
Controller → Service → Repository → Entity
```

### 2. DTO Pattern
```java
// Request DTO
public class CreateRequest {
    @NotBlank
    private String name;
}

// Response DTO
public class EntityResponse {
    private Long id;
    private String name;
}
```

### 3. Exception Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<?> handleNotFound(...) { ... }
}
```

### 4. Repository Pattern
```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {
    List<Entity> findByStatus(String status);
}
```

### 5. Service Layer
```java
@Service
@Transactional(readOnly = true)
public class EntityService {
    private final EntityRepository repository;

    public EntityResponse getById(Long id) { ... }
}
```

---

## Red Flags to Avoid

❌ **Fat Controllers** - Logic should be in services
❌ **Entity exposure** - Never return entities directly from controllers
❌ **Missing validation** - Always validate input
❌ **Swallowed exceptions** - Let exceptions propagate or log them
❌ **No tests** - Write tests for business logic

---

## Progress Checklist

### Week 1-2: PetClinic
- [ ] Understand @SpringBootApplication
- [ ] Trace request flow
- [ ] Study entity relationships
- [ ] Run tests

### Week 3-4: RealWorld
- [ ] Understand JWT flow
- [ ] Study DTO patterns
- [ ] Implement similar auth in practice project
- [ ] Write integration tests

### Week 5-7: Mall
- [ ] Understand Redis caching
- [ ] Study MyBatis mapping
- [ ] Implement security
- [ ] Add search feature

### Week 10-12: PiggyMetrics
- [ ] Understand service discovery
- [ ] Study circuit breaker pattern
- [ ] Containerize services
- [ ] Deploy to cloud

### Week 17-20: Metasfresh
- [ ] Understand DDD aggregates
- [ ] Study domain events
- [ ] Implement event sourcing basics
- [ ] Write technical design doc

---

## Learning Resources

| Resource | When to Use |
|----------|-------------|
| Spring Boot Docs | Reference for annotations |
| Baeldung | Tutorials for specific topics |
| Spring YouTube | Deep dives into features |
| Stack Overflow | Debugging specific errors |
| GitHub Issues | See how others solved problems |

---

**Remember:** The goal is not to memorize code—it's to recognize patterns and know when to apply them. Code every day, study every day, and the patterns will become intuitive.
