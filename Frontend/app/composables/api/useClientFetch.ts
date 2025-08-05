import type { FetchOptions } from "ofetch";

export function useClientFetch<T>(url: string, options: FetchOptions = {}) {
  const config = useRuntimeConfig();
  const token = useAuthStore().accessToken;

  const headers = {
    ...((options.headers as Record<string, string>) || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  return $fetch<T>(url, {
    ...options,
    baseURL: config.public.apiBase || "",
    headers,
  });
}
