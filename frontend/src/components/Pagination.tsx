type PaginationProps = {
  page: number;
  totalPages: number;
  onPageChange: (nextPage: number) => void;
  className?: string;
};

export function Pagination({ page, totalPages, onPageChange, className }: PaginationProps) {
  if (totalPages <= 1) return null;

  const canPrev = page > 0;
  const canNext = page + 1 < totalPages;

  return (
    <div className={className ?? 'mt-4 flex items-center gap-2'}>
      <button
        className="rounded-md border border-slate-300 px-3 py-1 text-sm disabled:cursor-not-allowed disabled:opacity-50"
        disabled={!canPrev}
        onClick={() => onPageChange(page - 1)}
        type="button"
      >
        ← Prev
      </button>
      <span className="text-sm text-slate-700">
        Page {page + 1} of {totalPages}
      </span>
      <button
        className="rounded-md border border-slate-300 px-3 py-1 text-sm disabled:cursor-not-allowed disabled:opacity-50"
        disabled={!canNext}
        onClick={() => onPageChange(page + 1)}
        type="button"
      >
        Next →
      </button>
    </div>
  );
}
