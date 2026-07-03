# Member Management System

Hệ thống quản lý thành viên (Requirement 3) — Spring Boot 3 + Thymeleaf + MySQL.

## Yêu cầu

- Java 17+
- Maven 3.9+
- MySQL 8.x

## Cài đặt nhanh

### 1. Database

MySQL sẽ tự tạo database `member_management` khi app khởi động (nếu user có quyền).

```sql
-- Tuỳ chọn: tạo thủ công
CREATE DATABASE IF NOT EXISTS member_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Cấu hình

Copy file mẫu và chỉnh mật khẩu MySQL:

```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

Hoặc dùng biến môi trường:

| Biến | Mặc định | Mô tả |
|------|----------|-------|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `member_management` | Tên database |
| `DB_USERNAME` | `root` | MySQL user |
| `DB_PASSWORD` | *(trống)* | MySQL password |
| `SERVER_PORT` | `8080` | HTTP port |

### 3. Chạy ứng dụng

```bash
# Với profile local (khuyến nghị)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Hoặc set DB_PASSWORD trực tiếp
DB_PASSWORD=your_password mvn spring-boot:run
```

Truy cập: http://localhost:8080

### 4. Build JAR

```bash
mvn -DskipTests package
java -jar target/member-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## Tài khoản mặc định

| Email | Mật khẩu | Role | Ghi chú |
|-------|----------|------|---------|
| `admin@slearn.local` | `Admin@12345` | ADMIN | Truy cập `/admin` |
| `user@slearn.local` | `User@12345` | USER | Tài khoản client cơ bản |
| `demo_a@slearn.local` | `User@12345` | USER | Leader Team Alpha |
| `demo_b@slearn.local` | `User@12345` | USER | Team Alpha |
| `demo_c@slearn.local` | `User@12345` | USER | Team Beta |
| `demo_d@slearn.local` | `User@12345` | USER | Leader Team Beta |
| `demo_e@slearn.local` | `User@12345` | USER | Team Beta |

> Dữ liệu mẫu (teams, positions, skills, projects) được seed tự động lần đầu chạy app.

## Chức năng chính

### Admin (`/admin`)

- CRUD: Users, Teams, Positions, Skills, Projects
- Quản lý thành viên team + lịch sử
- Activity Logs
- Export / Import CSV

### Client

- Profile cá nhân (`/profile`)
- Danh sách teams & thành viên (`/teams`)
- Xem profile thành viên (`/members/{id}`)

## Kiểm thử

```bash
# Chạy toàn bộ unit + integration tests (profile test, H2 in-memory)
mvn test

# Báo cáo coverage (JaCoCo)
open target/site/jacoco/index.html
```

**Test suite hiện có:** service unit tests (Mockito), controller slice (`@WebMvcTest`), pure unit (`CsvUtil`, security), integration smoke (`HomeIntegrationTest`). Profile `test` tắt `DataInitializer` để dữ liệu test ổn định.

## SunLint (code quality)

Theo chuẩn Sun* Engineering — cài Node deps trước: `npm install`.

```bash
# Basic
npm run lint              # sunlint --all --input=src
npm run lint:changed      # chỉ file git đã đổi
npm run lint:security     # rule security

# Advanced
npm run lint:eslint       # tích hợp ESLint
npm run lint:pr           # CI/PR: fail-on-new-violations

# Repo này (Spring Boot Java): src/ không có .ts/.js → dùng lint:all
npm run lint:all          # quét theme-kit + file JS/TS trong project
```

> SunLint phân tích **JS/TypeScript**. Java (`src/main/java`) dùng `mvn test` / Checkstyle riêng.

## Cấu trúc project

```
src/main/java/com/slearn/membermanagement/
├── config/          # Security, DataInitializer
├── controller/      # MVC controllers (admin + client)
├── dto/             # Form / View DTOs
├── entity/          # JPA entities
├── repository/      # Spring Data JPA
├── security/        # UserDetails, login/logout
└── service/         # Business logic
```

## License

Dự án học tập — Slearn Academy.
