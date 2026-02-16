import { useMutation, useQuery } from '@tanstack/react-query';
import { fetchLoans, fetchRecommendations, updatePreferences } from '../../api/libraryApi';
import type { PreferencesPayload } from '../../types/api';

export function useRecommendationsQuery(userId: number) {
  return useQuery({
    queryKey: ['recommendations', userId],
    queryFn: () => fetchRecommendations(userId, 0, 10),
    enabled: userId > 0,
  });
}

export function useUpdatePreferencesMutation(userId: number) {
  return useMutation({
    mutationFn: (payload: PreferencesPayload) => updatePreferences(userId, payload),
  });
}

export function useLoansQuery(userId: number) {
  return useQuery({
    queryKey: ['loans', userId],
    queryFn: () => fetchLoans(userId),
    enabled: userId > 0,
  });
}
