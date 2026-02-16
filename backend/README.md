# Backend (Spring Boot)

Реализован базовый Backend для `Library Preferences System` по API-контракту из `docs/api-v1.md`.

## Что реализовано

- REST API v1 (`/api/v1/...`) для:
  - auth: register/login/refresh/logout
  - books: CRUD + фильтрация и пагинация
  - loans: borrow/return/extend + список займов пользователя
  - ratings/reviews
  - preferences/recommendations
  - reservations (create/cancel/list) + availability notifications
  - admin users + role patch
- JPA-сущности и репозитории для основных таблиц.
- Flyway-миграции (`src/main/resources/db/migration`).
- JWT access token (Bearer) + refresh token (в БД).
- Конфигурация H2 (in-memory) для быстрого локального запуска.
- Подготовка к PostgreSQL/Redis/OAuth2 Client через зависимости и настройки.

## Запуск

```bash
# из корня репозитория поднять инфраструктуру
# docker compose up -d postgres redis

cd backend
mvn spring-boot:run
```

По умолчанию backend стартует на H2. Чтобы работать с PostgreSQL, передайте переменные окружения:

```bash
DB_URL=jdbc:postgresql://localhost:5432/library \
DB_DRIVER=org.postgresql.Driver \
DB_USER=library_user \
DB_PASSWORD=library_pass \
REDIS_HOST=localhost \
REDIS_PORT=6379 \
JWT_SECRET=<base64-or-plain-secret-at-least-32-bytes> \
JWT_ACCESS_EXPIRATION_SECONDS=3600 \
mvn spring-boot:run
```

## Тесты

```bash
cd backend
mvn test
```

## Maven 403 / проблемы с доступом к репозиторию

Если `mvn test` падает с `403 Forbidden` при загрузке из Maven Central, значит в окружении нет прямого выхода в интернет и нужен корпоративный mirror (Nexus/Artifactory).

1. Открой `backend/.mvn/settings.xml`.
2. Замени `https://REPLACE-WITH-YOUR-NEXUS-OR-ARTIFACTORY/repository/maven-public/` на рабочий URL mirror.
3. При необходимости раскомментируй `<server>` и передай креды через `MAVEN_REPO_USER`/`MAVEN_REPO_PASS`.
4. Запускай Maven так:

```bash
cd backend
mvn -s .mvn/settings.xml test
```
