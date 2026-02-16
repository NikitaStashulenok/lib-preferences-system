# Архитектура системы

## 1. Общая модель

Система реализуется как **монолитное веб-приложение**, разделённое на 2 независимых проекта:

- **Backend** — Spring Boot REST API
- **Frontend** — React SPA

Проекты имеют независимую сборку и деплой, взаимодействуют только по HTTP/HTTPS в формате JSON.

## 2. Архитектурный стиль Backend

Выбран подход **Hexagonal Architecture (Ports & Adapters)** в форме модульного монолита.

### 2.1 Слои

1. **Domain**
   - Сущности: Book, Author, User, Rating, Review, Loan, Genre, RecommendationProfile.
   - Value Objects: Email, BookId, UserId, RatingScore.
   - Доменные сервисы: RecommendationEngine, LoanPolicyService.
2. **Application (Use Cases / CQRS)**
   - Команды: register user, borrow book, return book, rate book, add review, refresh token, etc.
   - Запросы: get catalog, find recommendations, get profile history.
   - DTO на `record`.
3. **Ports**
   - Входные порты (use-case interfaces).
   - Выходные порты (repositories, external APIs, cache, message publishing).
4. **Adapters**
   - Inbound: REST controllers, security filters.
   - Outbound: JPA/JDBC repositories, Redis adapter, OAuth provider adapters.
5. **Infrastructure**
   - Конфигурации Spring, Security, Flyway migrations, Docker.

### 2.2 CQRS

Используется собственная реализация CQRS на Spring:

- `command`-слой для изменений состояния.
- `query`-слой для чтения (оптимизированные read-model запросы, включая пагинацию).
- Отдельные DTO для команд и запросов.

## 3. Шаблоны проектирования

Минимум 3 паттерна (кроме Repository/UoW):

1. **Strategy** — стратегии рекомендаций:
   - content-based,
   - collaborative-lite,
   - popularity fallback.
2. **Factory** — фабрика формирования рекомендательного пайплайна под пользователя.
3. **Observer** — доменные события (`BookRated`, `LoanCreated`) и подписчики (обновление профиля рекомендаций).
4. **Facade** (доп.) — `RecommendationFacade` для orchestration внешних API + внутренней модели.

## 4. Технологические требования и соответствие

- Java 21 LTS (допустимо Java 17).
- Spring Boot 3.x.
- PostgreSQL 15+.
- Redis для кеша.
- Flyway для миграций.
- JWT + Refresh tokens.
- RBAC + `@PreAuthorize`.

## 5. Frontend архитектура

### 5.1 Слои

- `app/` — bootstrap, роутинг, store.
- `pages/` — страницы (Catalog, Book, Profile, Admin).
- `features/` — бизнес-фичи (auth, recommendations, loans, reviews).
- `entities/` — типы и бизнес-модели.
- `shared/` — UI-kit, api-client, utils.

### 5.2 State management

- **Redux Toolkit**:
  - authSlice
  - catalogSlice
  - recommendationSlice
  - loansSlice

### 5.3 Работа с API

- Axios client с интерсептором JWT.
- Авто-refresh access token.
- Query params: `page`, `size`, `sort`, `direction`, filters.

## 6. API принципы

- REST (GET/POST/PUT/PATCH/DELETE).
- Versioning: `/api/v1/...`.
- Корректные HTTP status codes.
- Пагинация: `Pageable`/`Slice`.
- Фильтрация и сортировка через query params.
- OpenAPI (springdoc) — опционально, рекомендуется.

## 7. Интеграции

Минимум 2 внешних REST API:

1. **Google Books API** — метаданные и обложки.
2. **GitHub API (OAuth2 login)** или **Google Identity OIDC** для SSO.

## 8. Нефункциональные требования

- 95-й перцентиль времени ответа < 200 мс (целевая метрика).
- Структурированное логирование SLF4J + Logback.
- Метрики через Actuator + Micrometer (опционально).

## 9. Безопасность

- JWT auth (access + refresh).
- BCrypt password hashing.
- RBAC (`ROLE_USER`, `ROLE_LIBRARIAN`, `ROLE_ADMIN`).
- Ограничение методов по ролям и ownership-политикам.

## 10. Тестирование

- Unit: JUnit 5 + Mockito, покрытие бизнес-логики > 40%.
- Integration: MockMvc/TestRestTemplate + Testcontainers (PostgreSQL, Redis).

