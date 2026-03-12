type ConfirmModalProps = {
  open: boolean;
  title: string;
  description: string;
  confirmLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
};

export function ConfirmModal({ open, title, description, confirmLabel, onConfirm, onCancel }: ConfirmModalProps) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 p-4">
      <div className="w-full max-w-md rounded-xl bg-white p-5 shadow-xl">
        <h3 className="text-lg font-semibold text-slate-900">{title}</h3>
        <p className="mt-2 text-sm text-slate-600">{description}</p>
        <div className="mt-4 flex justify-end gap-2">
          <button className="rounded-md border border-slate-300 px-3 py-2 text-sm" onClick={onCancel} type="button">
            Cancel
          </button>
          <button className="rounded-md bg-rose-600 px-3 py-2 text-sm text-white" onClick={onConfirm} type="button">
            {confirmLabel ?? 'Delete'}
          </button>
        </div>
      </div>
    </div>
  );
}
