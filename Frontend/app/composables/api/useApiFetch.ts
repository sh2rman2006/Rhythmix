import type { UseFetchOptions } from "#app";

export function useApiFetch<T>(url: string, options: UseFetchOptions<T> = {}) {
  const config = useRuntimeConfig();
  const token = useCookie("access_token");

  const headers = { ...((options.headers as Record<string, string>) || {}) };

  if (token.value) {
    headers["Authorization"] = `Bearer ${token.value}`;
  }

  return useFetch<T>(url, {
    ...options,
    baseURL: config.public.apiBase || "",
    headers,
  });
}