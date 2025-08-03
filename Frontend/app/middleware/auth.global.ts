export default defineNuxtRouteMiddleware(async (to) => {
  if (import.meta.client) return;

  if (to.path === "/auth/login") return;

  const authStore = useAuthStore();

  const tokens = await authStore.serverRefreshTokens();

  if (!tokens) {
    return navigateTo("/auth/login", { replace: true });
  }

  await $fetch("/api/auth/set-cookie", {
    method: "POST",
    body: tokens,
  });

  authStore.setAccessToken(tokens.access_token);
  authStore.setRefreshToken(tokens.refresh_token);
});
