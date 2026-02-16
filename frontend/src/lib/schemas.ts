import { z } from 'zod';

export const authSchema = z.object({
  email: z.string().email('Введите корректный e-mail'),
  password: z.string().min(6, 'Минимум 6 символов'),
});

export const catalogFilterSchema = z.object({
  genre: z.string().optional().default(''),
  author: z.string().optional().default(''),
  userId: z.coerce.number().int().positive('User ID должен быть > 0'),
});

export const preferencesSchema = z.object({
  userId: z.coerce.number().int().positive('User ID должен быть > 0'),
  genres: z.string().min(1, 'Укажите хотя бы один жанр'),
  authors: z.string().min(1, 'Укажите хотя бы одного автора'),
});

export const loansSchema = z.object({
  userId: z.coerce.number().int().positive('User ID должен быть > 0'),
});

export type AuthFormValues = z.infer<typeof authSchema>;
export type CatalogFilterValues = z.infer<typeof catalogFilterSchema>;
export type PreferencesFormValues = z.infer<typeof preferencesSchema>;
export type LoansFormValues = z.infer<typeof loansSchema>;
