// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2025-07-15",
  devtools: { enabled: true },
  modules: [
    "@nuxt/eslint",
    "@nuxt/image",
    "@nuxt/ui",
    "@pinia/nuxt",
    "@vueuse/nuxt",
  ],

  ssr: true,

  css: ["~/assets/css/main.css"],

  eslint: {
    checker: true,
  },

  runtimeConfig: {
    public: {
      keycloakIssuer: process.env.KEYCLOAK_ISSUER,
      keycloakClientId: process.env.KEYCLOAK_CLIENT_ID,
      keycloakCallback: process.env.KEYCLOAK_CALLBACK,
      keycloakRealm: process.env.KEYCLOAK_REALM,
    },
  },
});
