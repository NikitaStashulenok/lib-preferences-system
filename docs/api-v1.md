# API v1 (черновой контракт)

Base URL: `/api/v1`

## Auth

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

## Books

- `GET /books?page=0&size=20&sort=title&direction=asc`
- `GET /books/{id}`
- `POST /books` (LIBRARIAN)
- `PUT /books/{id}` (LIBRARIAN)
- `PATCH /books/{id}` (LIBRARIAN)
- `DELETE /books/{id}` (ADMIN)

## Catalog filters

- `GET /books?genre=fantasy&author=tolkien&yearFrom=1950&yearTo=2024`

## Loans

- `POST /loans` — взять книгу
- `POST /loans/{id}/return` — вернуть книгу
- `POST /loans/{id}/extend` — продлить
- `GET /users/me/loans`

## Ratings & Reviews

- `POST /books/{id}/ratings`
- `PUT /books/{id}/ratings/me`
- `POST /books/{id}/reviews`
- `GET /books/{id}/reviews?page=0&size=10`

## Recommendations

- `GET /users/me/recommendations?page=0&size=20`
- `POST /users/me/preferences` — обновить предпочтения

## Admin

- `GET /admin/users?page=0&size=20`
- `PATCH /admin/users/{id}/roles`

## Стандарт ответа ошибки

```json
{
  "timestamp": "2026-01-10T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/books"
}
```

