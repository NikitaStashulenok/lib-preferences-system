import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';
import booksFiltersReducer from '../features/books/booksSlice';

function loadTokenState() {
  const raw = localStorage.getItem('auth');
  if (!raw) {
    return undefined;
  }

  try {
    return { auth: JSON.parse(raw) as { accessToken: string | null; refreshToken: string | null } };
  } catch {
    return undefined;
  }
}

export const store = configureStore({
  reducer: {
    auth: authReducer,
    booksFilters: booksFiltersReducer,
  },
  preloadedState: loadTokenState(),
});

store.subscribe(() => {
  localStorage.setItem('auth', JSON.stringify(store.getState().auth));
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
