export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();

  const loginWithProvider = (provider) => {
    const issuer = config.public.KEYCLOAK_ISSUER;
    const clientId = config.public.KEYCLOAK_CLIENT_ID;
    const redirectUri = config.public.KEYCLOAK_CALLBACK;

    const url =
      `${issuer}/protocol/openid-connect/auth` +
      `?client_id=${clientId}` +
      `&redirect_uri=${encodeURIComponent(redirectUri)}` +
      `&response_type=code` +
      `&scope=openid` +
      `&kc_idp_hint=${provider}`;

    window.location.href = url;
  };

  return {
    provide: {
      loginWithProvider,
    },
  };
});
