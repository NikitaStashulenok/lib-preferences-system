import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ConfirmModal } from '../components/ConfirmModal';
import { Pagination } from '../components/Pagination';
import { getBookCoverUrl } from '../api/libraryApi';
import { useDeleteBookMutation, useBookDetailsQuery, useBookReviewsQuery } from '../features/catalog/hooks';
import { useAppSelector } from '../app/hooks';
import { parseJwt } from '../lib/auth';

export function BookDetailsPage() {
  const { id } = useParams();
  const bookId = Number(id);
  const navigate = useNavigate();

  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const payload = parseJwt(accessToken);
  const roles = payload?.roles ?? [];
  const canManageBooks = roles.includes('ROLE_ADMIN') || roles.includes('ROLE_LIBRARIAN');

  const [reviewsPage, setReviewsPage] = useState(0);
  const [confirmDelete, setConfirmDelete] = useState(false);

  const detailsQuery = useBookDetailsQuery(Number.isNaN(bookId) ? null : bookId);
  const reviewsQuery = useBookReviewsQuery(Number.isNaN(bookId) ? null : bookId, reviewsPage, 5);
  const deleteBookMutation = useDeleteBookMutation();

  const stars = useMemo(() => {
    const avg = detailsQuery.data?.averageRating ?? 0;
    const rounded = Math.round(avg);
    return '★'.repeat(rounded) + '☆'.repeat(Math.max(0, 5 - rounded));
  }, [detailsQuery.data?.averageRating]);

  if (Number.isNaN(bookId)) {
    return <p className="text-sm text-red-700">Invalid book id.</p>;
  }

  if (detailsQuery.isLoading) {
    return <p className="text-sm text-slate-600">Loading book details...</p>;
  }

  if (detailsQuery.error || !detailsQuery.data) {
    return <p className="text-sm text-red-700">Не удалось загрузить детали книги.</p>;
  }

  const { book } = detailsQuery.data;

  return (
    <section className="rounded-xl bg-white p-5 shadow-sm">
      <div className="grid gap-6 md:grid-cols-[260px_1fr]">
        <img className="w-full rounded-lg border border-slate-200 object-cover" src={getBookCoverUrl(book.id)} alt={book.title} />

        <div>
          <div className="flex flex-wrap items-center justify-between gap-2">
            <h2 className="text-2xl font-bold text-slate-900">{book.title}</h2>
            {canManageBooks && (
              <div className="flex gap-2">
                <button className="rounded-md border border-slate-300 px-3 py-1 text-sm" onClick={() => navigate('/management')} type="button">
                  ✏️ Edit
                </button>
                <button className="rounded-md bg-rose-600 px-3 py-1 text-sm text-white" onClick={() => setConfirmDelete(true)} type="button">
                  🗑 Delete
                </button>
              </div>
            )}
          </div>

          <p className="mt-2 text-sm text-slate-700"><span className="font-semibold">Author:</span> {book.author}</p>
          <p className="text-sm text-slate-700"><span className="font-semibold">Year:</span> {book.publicationYear}</p>
          <p className="text-sm text-slate-700"><span className="font-semibold">Genres:</span> {book.genres.join(', ') || '—'}</p>
          <p className="text-sm text-slate-700"><span className="font-semibold">ISBN:</span> {book.isbn || '—'}</p>
          <p className="text-sm text-slate-700"><span className="font-semibold">Publisher:</span> {book.publisher || '—'}</p>
          <p className="text-sm text-slate-700"><span className="font-semibold">Language:</span> {book.language || '—'}</p>
          <p className="text-sm text-slate-700"><span className="font-semibold">Pages:</span> {book.pageCount || '—'}</p>
          <p className="mt-2 text-sm text-slate-700"><span className="font-semibold">Description:</span> {book.description || '—'}</p>

          <div className="mt-4 rounded-lg bg-slate-100 p-3">
            <p className="text-sm font-semibold text-slate-800">Average rating</p>
            <p className="text-lg text-amber-500">{stars}</p>
            <p className="text-sm text-slate-700">{detailsQuery.data.averageRating.toFixed(2)} / 5 ({detailsQuery.data.ratingsCount} ratings)</p>
          </div>
        </div>
      </div>

      <div className="mt-8">
        <h3 className="mb-3 text-lg font-semibold">Comments</h3>
        {reviewsQuery.isLoading && <p className="text-sm text-slate-600">Loading comments...</p>}
        {reviewsQuery.error && <p className="text-sm text-red-700">Не удалось загрузить комментарии.</p>}

        <div className="space-y-3">
          {reviewsQuery.data?.content.map((review: { id: number; userId: number; text: string; createdAt: string }) => (
            <article className="rounded-lg border border-slate-200 p-3" key={review.id}>
              <p className="text-sm text-slate-800">{review.text}</p>
              <p className="mt-1 text-xs text-slate-500">User #{review.userId} • {new Date(review.createdAt).toLocaleString()}</p>
            </article>
          ))}
        </div>

        <Pagination page={reviewsQuery.data?.number ?? reviewsPage} totalPages={reviewsQuery.data?.totalPages ?? 0} onPageChange={setReviewsPage} />
      </div>

      <ConfirmModal
        open={confirmDelete}
        title="Delete book"
        description="This action will permanently remove the book."
        onCancel={() => setConfirmDelete(false)}
        onConfirm={() => {
          deleteBookMutation.mutate(book.id, {
            onSuccess: () => {
              setConfirmDelete(false);
              navigate('/catalog');
            },
          });
        }}
      />
    </section>
  );
}
