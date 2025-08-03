<script setup lang="ts">
import { NuxtLink } from "#components";
import { useRecommendationApi } from "~/composables/api/useRecommendationApi";

const colorMode = useColorMode();
colorMode.preference = `dark`;
const isDark = computed((): boolean => colorMode.preference === "dark");

const data = await useRecommendationApi().getRecommendations();
</script>

<template>
  <div :class="{ 'dark-page': isDark }">
    <h1>Привет, Nuxt и Tailwind!</h1>
    <NuxtLink to="/auth/login">Войти</NuxtLink>
    <div>{{ data }}</div>
  </div>
</template>
