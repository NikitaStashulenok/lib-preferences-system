# Frontend (React + TypeScript + Vite)

Обновлённый SPA интерфейс для `Library Preferences System` с явным применением библиотек из стека проекта.

## Используемые библиотеки

- React 18
- TypeScript 5.3
- Vite 5
- TanStack Query 5
- Redux Toolkit
- React Router DOM 6
- Axios
- Tailwind CSS 3.4
- React Hook Form 7
- Zod 3

## Что реализовано

- Авторизация (login/register) с React Hook Form + Zod валидацией.
- Каталог книг с фильтрами, хранением фильтров в Redux Toolkit и загрузкой/мутациями через TanStack Query.
- Рекомендации и обновление предпочтений через формы (RHF+Zod) и TanStack Query mutations.
- Профиль с историей выдач (TanStack Query).
- Axios API client с Bearer-токеном из Redux state.
- Tailwind CSS utility-first стили вместо кастомной CSS-верстки.

## Запуск

```bash
cd frontend
npm install
npm run dev
```

Dev server проксирует `/api` на `http://localhost:8080`.
