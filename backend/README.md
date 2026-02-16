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
- Обработчик ошибок в унифицированном формате.
- Конфигурация H2 (in-memory) для быстрого локального запуска.

## Запуск

```bash
cd backend
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
