# Схема данных (PostgreSQL, 3НФ)

Минимум 8 связанных таблиц:

1. `users`
2. `roles`
3. `user_roles`
4. `authors`
5. `genres`
6. `books`
7. `book_genres`
8. `book_copies`
9. `loans`
10. `ratings`
11. `reviews`
12. `recommendation_profiles`
13. `refresh_tokens`

## Связи

- `users` M:N `roles` через `user_roles`.
- `authors` 1:M `books`.
- `books` M:N `genres` через `book_genres`.
- `books` 1:M `book_copies`.
- `users` 1:M `loans`.
- `book_copies` 1:M `loans`.
- `users` 1:M `ratings`, `books` 1:M `ratings`.
- `users` 1:M `reviews`, `books` 1:M `reviews`.
- `users` 1:1 `recommendation_profiles`.
- `users` 1:M `refresh_tokens`.

## Индексы (минимально)

- `books(title)`
- `books(author_id)`
- `loans(user_id, status)`
- `ratings(user_id, book_id)` unique
- `reviews(book_id, created_at)`
- `refresh_tokens(user_id, expires_at)`

