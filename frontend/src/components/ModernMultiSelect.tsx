import { useMemo, useState } from 'react';

type Option = { value: string; label: string };

type ModernMultiSelectProps = {
  label: string;
  options: Option[];
  values: string[];
  onChange: (next: string[]) => void;
  placeholder?: string;
};

export function ModernMultiSelect({ label, options, values, onChange, placeholder }: ModernMultiSelectProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [query, setQuery] = useState('');

  const selectedOptions = useMemo(() => options.filter((option) => values.includes(option.value)), [options, values]);
  const filtered = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    if (!normalized) return options;
    return options.filter((option) => option.label.toLowerCase().includes(normalized));
  }, [options, query]);

  const toggle = (value: string) => {
    if (values.includes(value)) {
      onChange(values.filter((item) => item !== value));
      return;
    }
    onChange([...values, value]);
  };

  return (
    <div className="grid gap-1 text-sm font-medium">
      <span>{label}</span>
      <button
        className="min-h-11 rounded-lg border border-slate-300 bg-slate-900 px-3 py-2 text-left text-white"
        onClick={() => setIsOpen((prev) => !prev)}
        type="button"
      >
        {selectedOptions.length ? selectedOptions.map((option) => option.label).join(', ') : <span className="text-slate-400">{placeholder ?? 'Select options'}</span>}
      </button>

      {selectedOptions.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {selectedOptions.map((option) => (
            <span key={option.value} className="rounded-full bg-indigo-100 px-2 py-1 text-xs font-semibold text-indigo-700">
              {option.label}
            </span>
          ))}
        </div>
      )}

      {isOpen && (
        <div className="rounded-lg border border-slate-700 bg-slate-900 p-2 text-white shadow-lg">
          <input
            className="mb-2 w-full rounded-md border border-slate-600 bg-slate-800 px-2 py-1 text-sm"
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search..."
            value={query}
          />
          <div className="max-h-56 overflow-auto">
            {filtered.map((option) => {
              const checked = values.includes(option.value);
              return (
                <button
                  className={`mb-1 flex w-full items-center justify-between rounded-md px-2 py-2 text-left text-sm ${
                    checked ? 'bg-indigo-600 text-white' : 'hover:bg-slate-800'
                  }`}
                  key={option.value}
                  onClick={() => toggle(option.value)}
                  type="button"
                >
                  <span>{option.label}</span>
                  {checked && <span>✓</span>}
                </button>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
