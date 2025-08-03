// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    "@nuxt/eslint",
    "@nuxt/image",
    "@nuxt/ui",
    "@pinia/nuxt",
    "@vueuse/nuxt",
  ],

  ssr: true,
  devtools: { enabled: true },

  css: ["~/assets/css/main.css"],

  runtimeConfig: {
    public: {
      keycloakIssuer: process.env.KEYCLOAK_ISSUER,
      keycloakClientId: process.env.KEYCLOAK_CLIENT_ID,
      apiBase: process.env.NUXT_PUBLIC_API_BASE || "http://localhost:8080/api",
    },
  },
  
  compatibilityDate: "2025-07-15",

  eslint: {
    checker: true,
  },
});
