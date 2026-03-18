import type { AxiosError } from 'axios';
import type { FieldPath, FieldValues, UseFormSetError } from 'react-hook-form';

export type ApiErrorPayload = {
  message?: string;
  error?: string;
  details?: string;
  fieldErrors?: Record<string, string>;
};

export function extractApiError(error: unknown, fallback: string): string {
  const axiosError = error as AxiosError<ApiErrorPayload>;
  const data = axiosError.response?.data;
  return data?.message ?? data?.details ?? data?.error ?? fallback;
}

export function applyServerFieldErrors<TFieldValues extends FieldValues>(
  error: unknown,
  setError: UseFormSetError<TFieldValues>,
): boolean {
  const axiosError = error as AxiosError<ApiErrorPayload>;
  const fieldErrors = axiosError.response?.data?.fieldErrors;
  if (!fieldErrors) {
    return false;
  }

  let applied = false;
  for (const [field, message] of Object.entries(fieldErrors)) {
    if (!message) continue;
    setError(field as FieldPath<TFieldValues>, { type: 'server', message });
    applied = true;
  }

  return applied;
}
