import type { UseFetchOptions } from "#app";

export function useApiFetch<T>(url: string, options: UseFetchOptions<T> = {}) {
  const config = useRuntimeConfig();
  const token = useAuthStore().accessToken;

  const headers = { ...((options.headers as Record<string, string>) || {}) };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  return useFetch<T>(url, {
    ...options,
    baseURL: config.public.apiBase || "",
    headers,
  });
}
