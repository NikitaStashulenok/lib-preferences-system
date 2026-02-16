import { apiClient } from './client';
import type { AuthResponse, Book, Loan, Page, PreferencesPayload } from '../types/api';

export async function login(email: string, password: string): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>('/auth/login', { email, password });
  return data;
}

export async function register(email: string, password: string): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>('/auth/register', { email, password });
  return data;
}

export async function fetchBooks(params: {
  page: number;
  size: number;
  genre?: string;
  author?: string;
}): Promise<Page<Book>> {
  const { data } = await apiClient.get<Page<Book>>('/books', { params });
  return data;
}

export async function fetchRecommendations(userId: number, page = 0, size = 10): Promise<Page<Book>> {
  const { data } = await apiClient.get<Page<Book>>(`/users/${userId}/recommendations`, { params: { page, size } });
  return data;
}

export async function updatePreferences(userId: number, payload: PreferencesPayload): Promise<void> {
  await apiClient.post(`/users/${userId}/preferences`, payload);
}

export async function borrowBook(userId: number, bookId: number): Promise<void> {
  await apiClient.post('/loans', { userId, bookId });
}

export async function fetchLoans(userId: number): Promise<Loan[]> {
  const { data } = await apiClient.get<Loan[]>(`/users/${userId}/loans`);
  return data;
}
