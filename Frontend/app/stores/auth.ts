interface KeycloakTokenResponse {
  access_token: string;
  expires_in: number;
  refresh_expires_in: number;
  refresh_token: string;
  token_type: string;
  id_token: string;
  "not-before-policy": number;
  session_state: string;
  scope: string;
}

export const useAuthStore = defineStore("auth", {
  state: () => ({
    accessToken: useCookie("access_token").value as string | null,
    refreshToken: useCookie("refresh_token").value as string | null,
    user: null as Record<string, any> | null,
    refreshTimer: null as ReturnType<typeof setInterval> | null,
  }),

  actions: {
    setAccessToken(token: string | null) {
      this.accessToken = token;
      useCookie("access_token").value = token;
    },

    setRefreshToken(token: string | null) {
      this.refreshToken = token;
      useCookie("refresh_token").value = token;
    },

    async login(email: string, password: string) {
      if (!email || !password) {
        throw new Error("Email и пароль обязательны для входа");
      }

      const config = useRuntimeConfig();
      const router = useRouter();

      const params = new URLSearchParams({
        username: email,
        password,
        grant_type: "password",
        client_id: config.public.keycloakClientId,
      });

      try {
        const data = await $fetch<KeycloakTokenResponse>(
          `${config.public.keycloakIssuer}/protocol/openid-connect/token`,
          {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params.toString(),
          }
        );

        this.setAccessToken(data.access_token);
        this.setRefreshToken(data.refresh_token);

        await $fetch("/api/auth/set-cookie", {
          method: "POST",
          body: {
            access_token: data.access_token,
            refresh_token: data.refresh_token,
          },
        });

        this.startRefreshTimer();

        await router.push("/");
      } catch (error: any) {
        console.error("Ошибка при входе:", error.message || error);
        throw error;
      }
    },

    startRefreshTimer() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
      }
      this.refreshTimer = setInterval(() => {
        this.refreshAccessToken();
      }, 4 * 60 * 1000);
    },

    async refreshAccessToken() {
      const config = useRuntimeConfig();

      if (!this.refreshToken) {
        console.warn("Нет refresh токена, нельзя обновить access token");
        return;
      }

      const params = new URLSearchParams({
        grant_type: "refresh_token",
        client_id: config.public.keycloakClientId,
        refresh_token: this.refreshToken,
      });

      try {
        const data = await $fetch<KeycloakTokenResponse>(
          `${config.public.keycloakIssuer}/protocol/openid-connect/token`,
          {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params.toString(),
          }
        );

        this.setAccessToken(data.access_token);
        this.setRefreshToken(data.refresh_token);

        await $fetch("/api/auth/set-cookie", {
          method: "POST",
          body: {
            access_token: data.access_token,
            refresh_token: data.refresh_token,
          },
        });

        console.log("Токен успешно обновлен");
      } catch (error) {
        console.error("Ошибка обновления токена:", error);
      }
    },

    async getUser() {
      const config = useRuntimeConfig();

      if (!this.accessToken) {
        throw new Error("Нет access token для запроса пользователя");
      }

      try {
        this.user = await $fetch(
          `${config.public.apiBase}/api/userService/me`,
          {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          }
        );
      } catch (error) {
        console.error("Ошибка при получении информации о пользователе:", error);
        throw error;
      }
    },

    logout() {
      this.setAccessToken(null);
      this.setRefreshToken(null);
      this.user = null;

      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
        this.refreshTimer = null;
      }

      const router = useRouter();
      router.push("/login");
    },

    async serverRefreshTokens(): Promise<{
      access_token: string;
      refresh_token: string;
    } | null> {

      const config = useRuntimeConfig();
      const cookie = useCookie("refresh_token");
      const refresh = cookie.value;

      if (!refresh) return null;

      const params = new URLSearchParams({
        grant_type: "refresh_token",
        client_id: config.public.keycloakClientId,
        refresh_token: refresh,
      });

      try {
        const data = await $fetch<KeycloakTokenResponse>(
          `${config.public.keycloakIssuer}/protocol/openid-connect/token`,
          {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params.toString(),
          }
        );

        return {
          access_token: data.access_token,
          refresh_token: data.refresh_token,
        };
      } catch (e) {
        console.error("Ошибка обновления токена в middleware:", e);
        return null;
      }
    },
  },
});
