# План реализации

## Этап 1. Инициализация

- Создать `backend/` и `frontend/` как независимые проекты.
- Подготовить docker-compose (postgres, redis).
- Настроить CI (build + test).

## Этап 2. Backend core

- Domain + Application + Ports.
- Auth (JWT + refresh).
- CRUD каталога и выдач.
- Flyway migrations.

## Этап 3. Recommendation module

- Strategy engine.
- Хранение профиля предпочтений.
- Обновление по событиям рейтинга/выдачи.

## Этап 4. External integrations

- Google Books API connector.
- OAuth2/OIDC login.

## Этап 5. Frontend SPA

- Auth screens.
- Catalog + filters + pagination.
- Recommendations page.
- Profile + history.

## Этап 6. Quality gates

- Unit и integration тесты.
- Checkstyle/SpotBugs/Sonar.
- Нагрузочный smoke-test.

