# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BookManager is a full-stack library management system built with Spring Boot 3.2.1 (Java 17) backend and Vue 3 frontend. It features book catalog management, borrowing/returning operations, user authentication, and admin dashboards.

## Development Commands

### Backend (Maven)
```bash
# Run the application
mvn spring-boot:run

# Build JAR
mvn clean package

# Windows: use mvnw.cmd instead of mvn
```

### Frontend (Vite)
```bash
cd coursefront

# Start dev server (port 3000, proxies /api to localhost:8080)
npm run dev

# Production build
npm run build
```

### Database Setup
1. MySQL on localhost:3306
2. Import schema: `coursemanager.sql`
3. Database name: `coursemanager`

## Architecture

### Backend (`src/main/java/top/xubol/bookmanager/`)

```
controller/    REST API endpoints (UserController, BookController, etc.)
service/       Business logic interfaces
service/impl/  Service implementations using MyBatis Plus ServiceImpl pattern
mapper/        MyBatis Plus mapper interfaces
entity/        Database entities (User, Book, Category, BorrowRecord, Announcement)
dto/           Request DTOs (LoginDto, BookSaveDTO, BorrowApplyDTO, etc.)
vo/            Response value objects (CategoryTreeVO)
common/        Shared utilities (Result, ResultCode, RequireRole annotation)
config/        Spring configuration (AuthenticationInterceptor, WebConfig, MybatisPlusConfig)
util/          Utilities (JwtUtils)
task/          Scheduled tasks (OverdueTask for daily overdue detection)
```

### Frontend (`coursefront/src/`)

```
api/           Axios API modules (book.js, borrow.js, category.js, etc.)
stores/        Pinia state management stores
views/         Page components organized by feature (auth/, book/, admin/, borrow/)
layout/        MainLayout.vue with navigation
components/    Shared components (SearchBox, CategorySelect)
utils/         request.js with Axios interceptors
router/        Vue Router with auth guards
```

## Key Patterns

### Authentication
- JWT tokens (24h expiration) stored in localStorage
- `@RequireRole("admin")` annotation for method-level authorization
- `AuthenticationInterceptor` validates Bearer tokens
- User roles: 0 (regular user), 1 (admin)

### API Response Format
```java
Result<T> { code, message, data }
```
- 200: success, 401: unauthorized, 403: forbidden, 500: error

### Data Access
- MyBatis Plus `ServiceImpl<Mapper, Entity>` pattern
- `QueryWrapper` for dynamic queries
- `Page<T>` for pagination (page, size parameters)

### Frontend State
- Pinia stores with `pinia-plugin-persistedstate` for persistence
- Axios interceptors inject Authorization header
- Vue Composition API with `<script setup>`

## API Design

- All endpoints prefixed with `/api`
- Admin endpoints: `/api/admin/*` require admin role
- CRUD: POST (create), GET (read), PUT (update), DELETE (delete)
- Pagination params: `page`, `size` → returns `IPage<T>`

## Configuration Files

- Backend: `src/main/resources/application.yml` (DB, file uploads, MyBatis Plus)
- Frontend: `coursefront/vite.config.js` (dev server proxy)
- Schema: `coursemanager.sql`
