import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useLoansQuery } from '../features/preferences/hooks';
import { loansSchema, type LoansFormValues } from '../lib/schemas';

export function ProfilePage() {
  const {
    register,
    watch,
    formState: { errors },
  } = useForm<LoansFormValues>({
    resolver: zodResolver(loansSchema),
    defaultValues: { userId: 1 },
  });

  const userId = watch('userId');
  const loansQuery = useLoansQuery(userId);

  return (
    <section className="rounded-xl bg-white p-5 shadow-sm">
      <h2 className="mb-4 text-xl font-bold">Profile & Loan History</h2>

      <div className="mb-4 max-w-xs">
        <label className="grid gap-1 text-sm font-medium">
          User ID
          <input className="rounded-md border border-slate-300 px-3 py-2" type="number" {...register('userId')} />
          {errors.userId && <span className="text-sm text-red-700">{errors.userId.message}</span>}
        </label>
      </div>

      {loansQuery.isLoading && <p className="text-sm text-slate-600">Loading loans...</p>}
      {loansQuery.error && <p className="text-sm text-red-700">Ошибка загрузки займов.</p>}

      <div className="overflow-auto">
        <table className="min-w-full border-collapse text-sm">
          <thead>
            <tr className="border-b border-slate-200 text-left">
              <th className="p-2">Loan ID</th>
              <th className="p-2">Book ID</th>
              <th className="p-2">Status</th>
              <th className="p-2">Borrowed</th>
              <th className="p-2">Due</th>
              <th className="p-2">Returned</th>
            </tr>
          </thead>
          <tbody>
            {loansQuery.data?.map((loan) => (
              <tr className="border-b border-slate-100" key={loan.id}>
                <td className="p-2">{loan.id}</td>
                <td className="p-2">{loan.bookId}</td>
                <td className="p-2">{loan.status}</td>
                <td className="p-2">{loan.borrowedAt}</td>
                <td className="p-2">{loan.dueDate}</td>
                <td className="p-2">{loan.returnedAt || '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
