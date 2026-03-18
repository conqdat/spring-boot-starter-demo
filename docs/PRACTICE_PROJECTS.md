# 🛠️ Practice Projects - Build Along

This folder contains step-by-step practice projects to build alongside studying the open-source projects.

---

## Project 1: Task Manager API (Beginner)

**Goal:** Master CRUD operations, JPA, and basic REST

### Features
- Create/Update/Delete tasks
- List tasks with pagination
- Mark task as complete
- Filter by status

### Tech Stack
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (dev) / PostgreSQL (prod)
- Lombok

### Code Structure
```
src/main/java/com/example/taskmanager/
├── TaskApplication.java
├── controller/
│   └── TaskController.java
├── service/
│   └── TaskService.java
├── repository/
│   └── TaskRepository.java
├── model/
│   └── Task.java
├── dto/
│   ├── TaskRequest.java
│   └── TaskResponse.java
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

### Key Endpoints
```
POST   /api/tasks          - Create task
GET    /api/tasks          - List all tasks
GET    /api/tasks/{id}     - Get task by ID
PUT    /api/tasks/{id}     - Update task
DELETE /api/tasks/{id}     - Delete task
PATCH  /api/tasks/{id}/complete - Mark complete
GET    /api/tasks?status=PENDING - Filter by status
```

### Learning Outcomes
- RESTful API design
- JPA entity mapping
- Exception handling
- DTO pattern
- Validation

---

## Project 2: Blog Platform (Beginner/Intermediate)

**Goal:** Master relationships, authentication, authorization

### Features
- User registration/login
- Create blog posts
- Comments on posts
- Like posts
- Follow users
- User feed

### Tech Stack
- Spring Boot 3.x
- Spring Security
- JWT authentication
- Spring Data JPA
- BCrypt password hashing

### Code Structure
```
src/main/java/com/example/blog/
├── BlogApplication.java
├── controller/
│   ├── AuthController.java
│   ├── PostController.java
│   ├── CommentController.java
│   └── UserController.java
├── service/
│   ├── AuthService.java
│   ├── PostService.java
│   ├── CommentService.java
│   └── UserService.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   └── CommentRepository.java
├── model/
│   ├── User.java
│   ├── Post.java
│   └── Comment.java
├── dto/
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── PostRequest.java
│   └── PostResponse.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthFilter.java
│   └── SecurityConfig.java
└── exception/
    └── GlobalExceptionHandler.java
```

### Key Endpoints
```
POST   /api/auth/register       - Register user
POST   /api/auth/login          - Login user
GET    /api/posts               - List posts (paginated)
POST   /api/posts               - Create post
GET    /api/posts/{id}          - Get post with comments
POST   /api/posts/{id}/comments - Add comment
POST   /api/posts/{id}/like     - Like post
GET    /api/users/{id}/feed     - Get user's feed
```

### Learning Outcomes
- Spring Security configuration
- JWT token flow
- Many-to-many relationships
- Password encoding
- Method-level security

---

## Project 3: Expense Tracker (Intermediate)

**Goal:** Master complex queries, transactions, reporting

### Features
- Track expenses by category
- Set budgets
- Generate reports
- Export to CSV
- Recurring expenses

### Tech Stack
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- OpenAPI (Swagger)
- CSV export (Apache Commons CSV)

### Code Structure
```
src/main/java/com/example/expense/
├── ExpenseApplication.java
├── controller/
│   ├── ExpenseController.java
│   ├── CategoryController.java
│   ├── BudgetController.java
│   └── ReportController.java
├── service/
│   ├── ExpenseService.java
│   ├── CategoryService.java
│   ├── BudgetService.java
│   └── ReportService.java
├── repository/
│   ├── ExpenseRepository.java
│   ├── CategoryRepository.java
│   └── BudgetRepository.java
├── model/
│   ├── Expense.java
│   ├── Category.java
│   └── Budget.java
├── dto/
│   ├── ExpenseRequest.java
│   ├── ExpenseResponse.java
│   └── ReportResponse.java
├── specification/
│   └── ExpenseSpecification.java
└── config/
    └── OpenApiConfig.java
```

### Key Endpoints
```
GET    /api/expenses              - List expenses (filtered)
POST   /api/expenses              - Create expense
GET    /api/reports/monthly       - Monthly summary
GET    /api/reports/by-category   - Category breakdown
POST   /api/reports/export        - Export CSV
GET    /api/budgets               - List budgets
POST   /api/budgets               - Create budget
```

### Learning Outcomes
- JPA Specifications for dynamic queries
- @Transactional boundaries
- Aggregation queries
- File export
- API documentation

---

## Project 4: URL Shortener (Intermediate/Advanced)

**Goal:** Master caching, analytics, async processing

### Features
- Shorten URLs
- Custom aliases
- Click tracking
- Analytics dashboard
- Link expiration
- QR code generation

### Tech Stack
- Spring Boot 3.x
- Redis (caching)
- Spring Data JPA
- Spring Async
- Lombok

### Code Structure
```
src/main/java/com/example/shortener/
├── UrlShortenerApplication.java
├── controller/
│   ├── UrlController.java
│   └── AnalyticsController.java
├── service/
│   ├── UrlService.java
│   ├── AnalyticsService.java
│   └── QrCodeService.java
├── repository/
│   └── UrlRepository.java
├── model/
│   └── UrlMapping.java
├── dto/
│   ├── ShortenRequest.java
│   └── UrlStatsResponse.java
├── config/
│   └── RedisConfig.java
└── async/
    └── AnalyticsAsyncHandler.java
```

### Key Endpoints
```
POST   /api/urls/shorten          - Shorten URL
GET    /api/urls/{shortCode}      - Get original URL
GET    /{shortCode}               - Redirect (no auth)
GET    /api/urls/{id}/stats       - Get analytics
POST   /api/urls/{id}/qr          - Generate QR code
DELETE /api/urls/{id}             - Delete URL
```

### Learning Outcomes
- Redis cache strategies
- @Async for background processing
- Custom hash generation
- Analytics tracking
- Cache eviction

---

## Project 5: E-commerce Backend (Advanced)

**Goal:** Master microservices, messaging, distributed systems

### Features
- Product catalog
- Shopping cart
- Order processing
- Payment integration
- Inventory management
- Notification service

### Tech Stack
- Spring Boot 3.x
- Spring Cloud
- Eureka service discovery
- API Gateway
- RabbitMQ/Kafka
- Resilience4j
- Docker

### Services
```
api-gateway/           - Route requests
product-service/       - Product catalog
cart-service/          - Shopping cart
order-service/         - Order management
payment-service/       - Payment processing
inventory-service/     - Stock management
notification-service/  - Email/SMS notifications
```

### Key Concepts
- Service discovery
- Circuit breaker pattern
- Event-driven architecture
- Saga pattern for distributed transactions
- API composition

---

## Project 6: Event Sourcing CQRS Demo (Expert)

**Goal:** Master advanced architectural patterns

### Features
- Event store
- Command handlers
- Query handlers
- Event replay
- Snapshotting
- Read/write separation

### Tech Stack
- Spring Boot 3.x
- Axon Framework (optional)
- Event Store DB
- PostgreSQL (read model)
- Kafka (event streaming)

### Code Structure
```
src/main/java/com/example/cqrs/
├── CqrsApplication.java
├── command/
│   ├── CreateOrderCommand.java
│   └── OrderCommandHandler.java
├── query/
│   ├── GetOrderQuery.java
│   └── OrderQueryHandler.java
├── event/
│   ├── OrderCreatedEvent.java
│   └── OrderEventHandler.java
├── aggregate/
│   └── OrderAggregate.java
├── readmodel/
│   └── OrderReadModel.java
└── writemodel/
    └── OrderWriteModel.java
```

### Learning Outcomes
- Event sourcing fundamentals
- CQRS pattern implementation
- Eventual consistency
- Event replay and debugging
- Read/write model separation

---

## 📝 How to Use These Projects

1. **Start small** - Pick Project 1 if you're beginner
2. **Code daily** - Even 30 minutes counts
3. **Break things** - Don't be afraid to make mistakes
4. **Read docs** - Spring documentation is excellent
5. **Test first** - Try TDD for at least one project
6. **Deploy early** - Get it running on cloud ASAP
7. **Refactor** - Your v1 will need refactoring, that's normal

---

## 🎯 Progression Path

```
Task Manager (1 week)
    ↓
Blog Platform (2 weeks)
    ↓
Expense Tracker (2 weeks)
    ↓
URL Shortener (2 weeks)
    ↓
E-commerce (4 weeks)
    ↓
CQRS Demo (4 weeks)
```

Total: ~15 weeks of hands-on coding

---

## 💡 Pro Tips

1. **Commit often** - Use git from day 1
2. **Write tests** - Aim for 80%+ coverage
3. **Use Lombok** - Reduces boilerplate
4. **Learn debugging** - Master breakpoint debugging
5. **Read others' code** - Study the top 10 projects
6. **Build in public** - Share progress on Twitter/LinkedIn
7. **Document decisions** - Keep ADRs (Architecture Decision Records)

---

**Ready?** Clone the starter template and start coding today!
