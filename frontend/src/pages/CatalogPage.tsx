import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useMemo, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { setFilters } from '../features/books/booksSlice';
import { BookCard } from '../components/BookCard';
import { catalogFilterSchema, type CatalogFilterValues } from '../lib/schemas';
import { useBooksQuery, useBorrowBookMutation } from '../features/catalog/hooks';

export function CatalogPage() {
  const dispatch = useAppDispatch();
  const filters = useAppSelector((state) => state.booksFilters);
  const [page] = useState(0);
  const [size] = useState(12);
  const params = useMemo(
    () => ({ page, size, genre: filters.genre || undefined, author: filters.author || undefined }),
    [filters.author, filters.genre, page, size],
  );

  const booksQuery = useBooksQuery(params);
  const borrowMutation = useBorrowBookMutation(params);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<CatalogFilterValues>({
    resolver: zodResolver(catalogFilterSchema),
    defaultValues: {
      genre: filters.genre,
      author: filters.author,
      userId: 1,
    },
  });

  const userId = watch('userId');

  const submitFilters = (values: CatalogFilterValues) => {
    dispatch(setFilters({ genre: values.genre ?? '', author: values.author ?? '' }));
  };

  return (
    <section className="rounded-xl bg-white p-5 shadow-sm">
      <h2 className="mb-4 text-xl font-bold">Catalog</h2>

      <form className="mb-4 grid gap-3 md:grid-cols-3" onSubmit={handleSubmit(submitFilters)}>
        <label className="grid gap-1 text-sm font-medium">
          Genre
          <input className="rounded-md border border-slate-300 px-3 py-2" {...register('genre')} placeholder="fantasy" />
        </label>

        <label className="grid gap-1 text-sm font-medium">
          Author
          <input className="rounded-md border border-slate-300 px-3 py-2" {...register('author')} placeholder="tolkien" />
        </label>

        <label className="grid gap-1 text-sm font-medium">
          User ID for borrow
          <input className="rounded-md border border-slate-300 px-3 py-2" type="number" {...register('userId')} />
          {errors.userId && <span className="text-sm text-red-700">{errors.userId.message}</span>}
        </label>

        <button className="w-fit rounded-md bg-slate-900 px-4 py-2 text-white" type="submit">
          Apply filters
        </button>
      </form>

      {booksQuery.isLoading && <p className="text-sm text-slate-600">Loading books...</p>}
      {booksQuery.error && <p className="text-sm text-red-700">Ошибка загрузки каталога.</p>}

      <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
        {booksQuery.data?.content.map((book) => (
          <BookCard
            key={book.id}
            book={book}
            onBorrow={(bookId) => borrowMutation.mutate({ userId, bookId })}
          />
        ))}
      </div>

      {borrowMutation.isSuccess && <p className="mt-3 text-sm text-green-700">Книга успешно выдана.</p>}
      {borrowMutation.error && <p className="mt-3 text-sm text-red-700">Ошибка выдачи книги.</p>}
    </section>
  );
}
