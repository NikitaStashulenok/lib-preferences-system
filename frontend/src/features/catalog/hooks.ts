import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { borrowBook, fetchBooks } from '../../api/libraryApi';

export function useBooksQuery(params: { page: number; size: number; genre?: string; author?: string }) {
  return useQuery({
    queryKey: ['books', params],
    queryFn: () => fetchBooks(params),
  });
}

export function useBorrowBookMutation(params: { page: number; size: number; genre?: string; author?: string }) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ userId, bookId }: { userId: number; bookId: number }) => borrowBook(userId, bookId),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['books', params] });
    },
  });
}
