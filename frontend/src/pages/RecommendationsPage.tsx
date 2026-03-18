import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { BookCard } from '../components/BookCard';
import { ModernMultiSelect } from '../components/ModernMultiSelect';
import { Pagination } from '../components/Pagination';
import { useRecommendationsQuery, useUpdatePreferencesMutation } from '../features/preferences/hooks';
import { preferencesSchema, type PreferencesFormValues } from '../lib/schemas';
import { parseJwt } from '../lib/auth';
import { useAppSelector } from '../app/hooks';
import { useCatalogMetaQuery } from '../features/catalog/hooks';
import { extractApiError } from '../lib/apiErrors';
import type { RecommendationSource } from '../types/api';

export function RecommendationsPage() {
  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const payload = parseJwt(accessToken);
  const userId = payload?.uid ?? null;

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(12);
  const [source, setSource] = useState<RecommendationSource>('all');

  const metaQuery = useCatalogMetaQuery();
  const recommendationsQuery = useRecommendationsQuery(userId, page, size, source);
  const preferencesMutation = useUpdatePreferencesMutation(userId);

  const {
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<PreferencesFormValues>({
    resolver: zodResolver(preferencesSchema),
    defaultValues: {
      genres: [],
      authors: [],
    },
  });

  const selectedGenres = watch('genres') ?? [];
  const selectedAuthors = watch('authors') ?? [];

  const onSave = (values: PreferencesFormValues) => {
    setPage(0);
    preferencesMutation.mutate({
      preferredGenres: values.genres,
      favoriteAuthors: values.authors,
    });
  };

  return (
    <section className="rounded-xl bg-white p-5 shadow-sm">
      <h2 className="mb-4 text-xl font-bold">Recommendations</h2>

      <form className="grid gap-3" onSubmit={handleSubmit(onSave)}>
        <ModernMultiSelect
          label="Preferred genres"
          options={(metaQuery.data?.genres ?? []).map((genre) => ({ value: genre, label: genre }))}
          placeholder="Choose preferred genres"
          values={selectedGenres}
          onChange={(next) => setValue('genres', next, { shouldDirty: true })}
        />
        {errors.genres && <span className="text-sm text-red-700">{errors.genres.message}</span>}

        <ModernMultiSelect
          label="Favorite authors"
          options={(metaQuery.data?.authors ?? []).map((author) => ({ value: author, label: author }))}
          placeholder="Choose favorite authors"
          values={selectedAuthors}
          onChange={(next) => setValue('authors', next, { shouldDirty: true })}
        />
        {errors.authors && <span className="text-sm text-red-700">{errors.authors.message}</span>}

        <button className="w-fit rounded-md bg-indigo-600 px-4 py-2 text-white" type="submit">
          Save preferences
        </button>
      </form>

      {preferencesMutation.isSuccess && <p className="mt-3 text-sm text-green-700">Preferences updated.</p>}
      {preferencesMutation.error && <p className="mt-3 text-sm text-red-700">{extractApiError(preferencesMutation.error, 'Ошибка обновления предпочтений.')}</p>}

      <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
        <div className="text-sm text-slate-600">Найдено рекомендаций: {recommendationsQuery.data?.totalElements ?? 0}</div>
        <div className="flex gap-3">
          <label className="flex items-center gap-2 text-sm">
            <span className="text-slate-600">Источник</span>
            <select
              className="rounded-md border border-slate-300 px-2 py-1"
              value={source}
              onChange={(event) => {
                setSource(event.target.value as RecommendationSource);
                setPage(0);
              }}
            >
              <option value="all">Все</option>
              <option value="user">Пользовательские</option>
              <option value="system">Системные</option>
            </select>
          </label>

          <label className="flex items-center gap-2 text-sm">
            <span className="text-slate-600">На странице</span>
            <select
              className="rounded-md border border-slate-300 px-2 py-1"
              value={size}
              onChange={(event) => {
                setSize(Number(event.target.value));
                setPage(0);
              }}
            >
              <option value={12}>12</option>
              <option value={24}>24</option>
              <option value={36}>36</option>
            </select>
          </label>
        </div>
      </div>

      <div className="mt-5 grid grid-cols-1 gap-3 md:grid-cols-3">
        {recommendationsQuery.data?.content.map((item) => (
          <BookCard key={item.book.id} book={item.book} isRecommended recommendationTags={item.sourceTags} />
        ))}
      </div>

      <Pagination
        className="mt-4"
        page={recommendationsQuery.data?.number ?? page}
        totalPages={recommendationsQuery.data?.totalPages ?? 0}
        onPageChange={setPage}
      />
    </section>
  );
}
