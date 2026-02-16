import type { Book } from '../types/api';

type BookCardProps = {
  book: Book;
  onBorrow?: (bookId: number) => void;
};

export function BookCard({ book, onBorrow }: BookCardProps) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <h3 className="mb-2 text-lg font-semibold">{book.title}</h3>
      <p className="text-sm text-slate-700">
        <span className="font-semibold">Author:</span> {book.author}
      </p>
      <p className="text-sm text-slate-700">
        <span className="font-semibold">Year:</span> {book.publicationYear}
      </p>
      <p className="text-sm text-slate-700">
        <span className="font-semibold">Genres:</span> {book.genres?.join(', ') || '—'}
      </p>
      <p className="mb-3 text-sm text-slate-700">
        <span className="font-semibold">Available:</span> {book.availableCopies}/{book.totalCopies}
      </p>
      {onBorrow && (
        <button
          className="rounded-md bg-slate-900 px-3 py-2 text-sm text-white disabled:cursor-not-allowed disabled:opacity-50"
          disabled={book.availableCopies < 1}
          onClick={() => onBorrow(book.id)}
        >
          Borrow
        </button>
      )}
    </article>
  );
}
