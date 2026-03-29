# 🧪 Spring Boot Unit Testing — Lộ Trình Học Từ Cơ Bản Đến Nâng Cao

> **Mục tiêu:** Nắm vững toàn bộ testing trong Spring Boot theo 5 giai đoạn, từ JUnit 5 cơ bản đến CI/CD với JaCoCo.
>
> **Nguồn tham khảo chính:**
> - [Baeldung — Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing)
> - [HowToDoInJava — Spring Boot Testing](https://howtodoinjava.com/spring-boot2/testing/spring-boot-2-junit-5/)
> - [BezKoder — WebMvcTest](https://www.bezkoder.com/spring-boot-webmvctest/)
> - [Wim Deblauwe — Production-ready Testing](https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/)
> - [DEV Community — JUnit5 + Mockito + Testcontainers](https://dev.to/sivalabs/testing-springboot-applications-4i5p)
> - [Spring Official Guide](https://spring.io/guides/gs/testing-web/)

---

## 📌 Testing Pyramid — Hiểu Trước Khi Học

```
         ▲
        /E2E\         ← Ít nhất (chậm, tốn kém)
       /------\
      /  Integ  \     ← Vừa phải
     /------------\
    /  Unit Tests  \  ← Nhiều nhất (nhanh, rẻ, isolated)
   /________________\
```

| Loại Test | Annotation | Tốc độ | Scope |
|---|---|---|---|
| Unit Test | `@ExtendWith(MockitoExtension.class)` | ⚡ Rất nhanh | 1 class |
| Slice Test | `@WebMvcTest`, `@DataJpaTest` | 🚀 Nhanh | 1 layer |
| Integration Test | `@SpringBootTest` | 🐢 Chậm hơn | Full context |

---

## ⚙️ Setup Project

### Dependency (pom.xml)

```xml
<!-- Spring Boot Test Starter — bao gồm JUnit 5, Mockito, AssertJ, MockMvc -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 In-memory DB — dùng cho @DataJpaTest -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### `spring-boot-starter-test` bao gồm:
- ✅ **JUnit 5** — Framework test chính
- ✅ **Mockito** — Mock dependencies
- ✅ **AssertJ** — Fluent assertions (dễ đọc hơn JUnit assert)
- ✅ **Hamcrest** — Matchers
- ✅ **JSONassert / JsonPath** — Kiểm tra JSON response
- ✅ **MockMvc** — Test HTTP layer không cần start server

### Cấu trúc thư mục chuẩn

```
src/
├── main/java/com/example/
│   ├── controller/  UserController.java
│   ├── service/     UserService.java
│   └── repository/  UserRepository.java
└── test/java/com/example/
    ├── controller/  UserControllerTest.java
    ├── service/     UserServiceTest.java
    └── repository/  UserRepositoryTest.java
```

---

## 🟢 Giai Đoạn 1 — JUnit 5 + Mockito (Unit Test thuần)

> **Mục tiêu:** Test từng class độc lập, không cần Spring context.

### 1.1 Annotations Cốt Lõi của JUnit 5

| Annotation | Mục đích |
|---|---|
| `@Test` | Đánh dấu method là test case |
| `@BeforeEach` | Chạy trước mỗi test (setup) |
| `@AfterEach` | Chạy sau mỗi test (teardown) |
| `@BeforeAll` | Chạy 1 lần trước tất cả test (phải `static`) |
| `@DisplayName` | Đặt tên mô tả cho test |
| `@Disabled` | Bỏ qua test |
| `@ParameterizedTest` | Test với nhiều bộ input khác nhau |

### 1.2 Annotations Cốt Lõi của Mockito

| Annotation | Mục đích |
|---|---|
| `@ExtendWith(MockitoExtension.class)` | Kích hoạt Mockito trong JUnit 5 |
| `@Mock` | Tạo mock object (giả toàn bộ behavior) |
| `@InjectMocks` | Tạo object thật + tự động inject các `@Mock` vào |
| `@Spy` | Wrap object thật, mock 1 số method cụ thể |
| `@Captor` | Capture argument truyền vào mock |

### 1.3 Ví Dụ — Unit Test Service Layer

```java
// UserService.java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
```

```java
// UserServiceTest.java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;  // Giả repository

    @InjectMocks
    private UserService userService;        // Object thật, inject mock vào

    @Test
    @DisplayName("Khi gọi getAllUsers() thì trả về đúng danh sách")
    void whenGetAllUsers_thenReturnList() {
        // Given (Arrange)
        List<User> mockUsers = List.of(
            new User(1L, "Dat", "dat@email.com"),
            new User(2L, "An",  "an@email.com")
        );
        when(userRepository.findAll()).thenReturn(mockUsers);

        // When (Act)
        List<User> result = userService.getAllUsers();

        // Then (Assert)
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Dat");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Khi không tìm thấy user thì throw exception")
    void whenUserNotFound_thenThrowException() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");
    }
}
```

### 1.4 BDD Style — given/when/then với Mockito

```java
// Thay vì: when(...).thenReturn(...)
// Dùng BDD style cho dễ đọc:
import static org.mockito.BDDMockito.*;

given(userRepository.findAll()).willReturn(mockUsers);

// then:
then(userRepository).should(times(1)).findAll();
```

### 1.5 Mockito Argument Matchers

```java
// Match bất kỳ giá trị nào
when(userRepository.save(any(User.class))).thenReturn(savedUser);

// Match giá trị cụ thể
when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

// Match theo điều kiện
when(repo.findByName(argThat(name -> name.startsWith("D")))).thenReturn(users);
```

### 1.6 Verify — Kiểm Tra Mock Có Được Gọi Đúng Không

```java
verify(userRepository, times(1)).save(any(User.class));  // Gọi đúng 1 lần
verify(userRepository, never()).delete(any());            // Không được gọi
verify(userRepository, atLeast(2)).findAll();             // Gọi ít nhất 2 lần
```

### 📚 Tài liệu Phase 1
- [Baeldung: Mockito Annotations](https://www.baeldung.com/mockito-annotations)
- [HowToDoInJava: JUnit 5 + Mockito Service Test](https://howtodoinjava.com/spring-boot2/testing/spring-boot-2-junit-5/)

---

## 🟡 Giai Đoạn 2 — Slice Testing theo Layer

> **Mục tiêu:** Test từng layer (Controller / Repository) trong partial Spring context — nhanh hơn `@SpringBootTest`, chính xác hơn unit test thuần.

### 2.1 Test Controller với `@WebMvcTest`

**Cách hoạt động:**
- Chỉ load Spring MVC infrastructure (Controller, Filter, ControllerAdvice)
- **KHÔNG** load `@Service`, `@Repository` → phải `@MockBean`
- Tự động cấu hình `MockMvc`

```java
// UserControllerTest.java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;           // Auto-configured

    @MockBean
    private UserService userService;   // Mock service — Spring quản lý

    @Autowired
    private ObjectMapper objectMapper; // Serialize/deserialize JSON

    @Test
    @DisplayName("GET /users — trả về 200 và danh sách users")
    void whenGetAllUsers_thenReturn200() throws Exception {
        // Given
        List<User> users = List.of(new User(1L, "Dat", "dat@email.com"));
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("Dat")));
    }

    @Test
    @DisplayName("POST /users — tạo user mới trả về 201")
    void whenCreateUser_thenReturn201() throws Exception {
        // Given
        User newUser = new User(null, "Dat", "dat@email.com");
        User savedUser = new User(1L, "Dat", "dat@email.com");
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Dat"));
    }

    @Test
    @DisplayName("GET /users/{id} — không tìm thấy trả về 404")
    void whenUserNotFound_thenReturn404() throws Exception {
        // Given
        when(userService.getUserById(99L))
            .thenThrow(new UserNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(get("/users/99"))
            .andExpect(status().isNotFound());
    }
}
```

**Bảng MockMvc Matchers hay dùng:**

| Matcher | Mục đích |
|---|---|
| `status().isOk()` | HTTP 200 |
| `status().isCreated()` | HTTP 201 |
| `status().isNotFound()` | HTTP 404 |
| `jsonPath("$.name")` | Kiểm tra field trong JSON |
| `jsonPath("$", hasSize(3))` | Kiểm tra array size |
| `content().contentType(...)` | Kiểm tra Content-Type |
| `header().string("Location", ...)` | Kiểm tra response header |

### 2.2 Test Repository với `@DataJpaTest`

**Cách hoạt động:**
- Chỉ load JPA layer (entities, repositories)
- Dùng in-memory H2 database mặc định
- Tự động rollback sau mỗi test
- `TestEntityManager` để setup data

```java
// UserRepositoryTest.java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Helper để insert data

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Tìm user theo email — trả về đúng user")
    void whenFindByEmail_thenReturnUser() {
        // Given — insert test data
        User user = new User(null, "Dat", "dat@email.com");
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByEmail("dat@email.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Dat");
    }

    @Test
    @DisplayName("Không tồn tại email — trả về empty")
    void whenEmailNotExist_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("notexist@email.com");
        assertThat(found).isEmpty();
    }
}
```

> ⚠️ **Lưu ý:** `@DataJpaTest` dùng H2 in-memory. Để test với PostgreSQL/MySQL thật → dùng **Testcontainers** ở Phase 4.

### 📚 Tài liệu Phase 2
- [Baeldung: @WebMvcTest](https://www.baeldung.com/spring-boot-testing#unit-testing-with-webmvctest)
- [BezKoder: WebMvcTest Tutorial](https://www.bezkoder.com/spring-boot-webmvctest/)
- [HowToDoInJava: @DataJpaTest](https://howtodoinjava.com/spring-boot2/testing/spring-boot-2-junit-5/)

---

## 🔵 Giai Đoạn 3 — Integration Test với `@SpringBootTest`

> **Mục tiêu:** Test toàn bộ application stack cùng nhau — load full Spring context.

### 3.1 Khi nào dùng `@SpringBootTest`?

✅ Dùng khi cần:
- Test bean wiring và dependency injection
- Test nhiều layer phối hợp với nhau
- Test security configuration
- Test application startup

❌ KHÔNG dùng khi:
- Test logic đơn giản của một service
- Test request mapping của controller (dùng `@WebMvcTest`)
- Test JPA queries (dùng `@DataJpaTest`)

### 3.2 Các Mode của `@SpringBootTest`

```java
// Mode 1: Không start HTTP server (dùng MockMvc) — PHỔ BIẾN NHẤT
@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
}

// Mode 2: Start server thật trên random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
}
```

### 3.3 Ví Dụ Integration Test

```java
// UserIntegrationTest.java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Dùng application-test.yml
@Transactional           // Rollback sau mỗi test
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.save(new User(null, "Dat", "dat@email.com"));
    }

    @Test
    @DisplayName("Integration: GET /users — lấy từ DB thật")
    void whenGetUsers_thenReturnFromDatabase() throws Exception {
        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Dat"));
    }
}
```

### 3.4 `@MockBean` trong Integration Test

```java
// Khi muốn mock 1 bean nhưng vẫn load full context
@SpringBootTest
class UserServiceIntegrationTest {

    @MockBean
    private EmailService emailService; // Mock external service

    @Autowired
    private UserService userService;   // Dùng thật

    @Test
    void whenCreateUser_thenEmailServiceCalled() {
        userService.createUser(new User(...));
        verify(emailService, times(1)).sendWelcomeEmail(anyString());
    }
}
```

### 3.5 Best Practices

```
✅ Tách file: đặt tên *IT.java hoặc *IntegrationTest.java
✅ Dùng @ActiveProfiles("test") để load config riêng
✅ Dùng @Transactional để tự rollback DB sau mỗi test
✅ Giữ integration test ÍT hơn unit test
❌ Không dùng @SpringBootTest cho mọi thứ — rất chậm
```

### 📚 Tài liệu Phase 3
- [Baeldung: Integration Testing](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)
- [Djamware: SpringBootTest Guide](https://www.djamware.com/post/unit-testing-in-spring-boot-with-junit-and-mockito)

---

## 🟣 Giai Đoạn 4 — Test Database Thật với Testcontainers

> **Mục tiêu:** Thay H2 bằng PostgreSQL/MySQL thật trong Docker — tránh false positive khi test.

### 4.1 Vì Sao Cần Testcontainers?

> H2 có thể pass nhưng code vẫn fail trên PostgreSQL thật vì:
> - Khác nhau về SQL syntax
> - Khác nhau về constraint behavior
> - Khác nhau về type casting

### 4.2 Setup Dependency

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<!-- BOM để quản lý version -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-bom</artifactId>
    <version>1.19.3</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

### 4.3 Cách Dùng Testcontainers

```java
// UserRepositoryIT.java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Tắt H2
@Testcontainers
class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenSaveUser_thenCanFindById() {
        User saved = userRepository.save(new User(null, "Dat", "dat@email.com"));
        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("dat@email.com");
    }
}
```

### 4.4 Dùng `@SpringBootTest` + Testcontainers

```java
// FullStackIT.java — Test toàn bộ stack với DB thật
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FullStackIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void whenCreateAndGetUser_thenSuccess() {
        // POST
        User newUser = new User(null, "Dat", "dat@email.com");
        ResponseEntity<User> createResponse = restTemplate
            .postForEntity("/users", newUser, User.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // GET
        Long id = createResponse.getBody().getId();
        ResponseEntity<User> getResponse = restTemplate
            .getForEntity("/users/" + id, User.class);
        assertThat(getResponse.getBody().getName()).isEqualTo("Dat");
    }
}
```

> ⚠️ **Yêu cầu:** Docker phải đang chạy khi run test.

### 📚 Tài liệu Phase 4
- [Testcontainers Official Docs](https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/)
- [DEV Community: Spring Boot + Testcontainers](https://dev.to/sivalabs/testing-springboot-applications-4i5p)
- [Wim Deblauwe: Production-ready Testing](https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/)

---

## 🔴 Giai Đoạn 5 — Test Coverage với JaCoCo

> **Mục tiêu:** Đo lường coverage, enforce minimum threshold, tích hợp CI/CD.

### 5.1 Setup JaCoCo Plugin

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <!-- Bước 1: Chuẩn bị agent thu thập data -->
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <!-- Bước 2: Generate report sau khi test xong -->
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <!-- Bước 3: Enforce minimum coverage (fail build nếu không đạt) -->
        <execution>
            <id>check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum> <!-- 80% line coverage -->
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum> <!-- 70% branch coverage -->
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 5.2 Chạy và Xem Report

```bash
# Chạy test + generate report
mvn clean verify

# Report HTML nằm ở:
# target/site/jacoco/index.html
```

### 5.3 Exclude Khỏi Coverage

```xml
<!-- Exclude DTO, config, generated code -->
<configuration>
    <excludes>
        <exclude>com/example/dto/**</exclude>
        <exclude>com/example/config/**</exclude>
        <exclude>**/generated/**</exclude>
    </excludes>
</configuration>
```

```java
// Hoặc dùng annotation trực tiếp trên class/method
@lombok.Generated  // Lombok tự thêm nếu dùng @Builder, @Data
@ExcludeFromJacocoGeneratedReport
public class SomeUtilClass { ... }
```

### 5.4 Hiểu Coverage Metrics

| Metric | Ý nghĩa | Target gợi ý |
|---|---|---|
| **Line Coverage** | % dòng code được chạy qua | ≥ 80% |
| **Branch Coverage** | % nhánh if/else được cover | ≥ 70% |
| **Method Coverage** | % methods được test | ≥ 85% |
| **Class Coverage** | % classes được test | ≥ 90% |

### 5.5 Tích Hợp CI/CD (GitHub Actions)

```yaml
# .github/workflows/ci.yml
name: CI - Test & Coverage

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      # Nếu cần DB thật (hoặc dùng Testcontainers thay thế)
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run Tests with Coverage
        run: mvn clean verify

      - name: Upload Coverage Report
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: target/site/jacoco/
```

### 📚 Tài liệu Phase 5
- [Baeldung: JaCoCo](https://www.baeldung.com/jacoco)
- [JaCoCo Official Docs](https://www.jacoco.org/jacoco/trunk/doc/)

---

## 🏆 Cheat Sheet — Annotations Tổng Hợp

```
📦 Pure Unit Test (không cần Spring)
└── @ExtendWith(MockitoExtension.class)
    ├── @Mock              → giả object
    ├── @InjectMocks       → object thật + inject mock
    ├── @Spy               → wrap object thật
    └── @Captor            → bắt argument

🍕 Slice Test (partial Spring context)
├── @WebMvcTest            → test Controller + MockMvc
│   └── @MockBean          → mock Service trong Spring context
└── @DataJpaTest           → test Repository + H2
    └── TestEntityManager  → setup test data

🌍 Integration Test (full Spring context)
└── @SpringBootTest
    ├── @AutoConfigureMockMvc → dùng với MockMvc
    ├── @MockBean             → mock 1 bean cụ thể
    ├── @ActiveProfiles       → chọn profile test
    └── @Transactional        → auto rollback

🐳 Real Database Test
└── @Testcontainers
    └── @Container + PostgreSQLContainer

📊 Coverage
└── JaCoCo Maven Plugin
    └── mvn clean verify → target/site/jacoco/index.html
```

---

## 📅 Lịch Học Gợi Ý (5 tuần)

| Tuần | Giai đoạn | Bài tập thực hành |
|------|-----------|-------------------|
| 1 | Phase 1: JUnit 5 + Mockito | Viết test cho toàn bộ Service layer của project hiện tại |
| 2 | Phase 2: @WebMvcTest | Viết test cho tất cả Controller endpoints |
| 2 | Phase 2: @DataJpaTest | Viết test cho custom JPA queries |
| 3 | Phase 3: @SpringBootTest | Viết 3-5 integration test quan trọng nhất |
| 4 | Phase 4: Testcontainers | Migrate @DataJpaTest sang PostgreSQL thật |
| 5 | Phase 5: JaCoCo | Setup CI với coverage gate ≥ 80% |

---

## 💡 Senior-Level Tips

1. **Test behavior, không test implementation** — test "kết quả", không test "cách làm"
2. **Mỗi test chỉ assert 1 điều** — dễ debug khi fail
3. **Dùng BDD style** (`given/when/then`) — dễ đọc như documentation
4. **Tên test phải tự mô tả** — `whenUserNotFound_thenThrowException` thay vì `test1`
5. **Giữ test FAST** — unit test < 100ms, integration test < 5s
6. **Test negative cases** — không chỉ happy path
7. **Không mock quá nhiều** — nếu phải mock > 5 dependencies, class quá lớn → refactor