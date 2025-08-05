<script setup lang="ts">
import { NuxtLink } from "#components";
import { useUserApi } from "~/composables/api/useUserApi";
import type { UserDto } from "~/types/dto/UserDto";

const user: UserDto = await useUserApi().getMeSSR();
</script>

<template>
  <div>
    <h1>Привет, Nuxt и Tailwind!</h1>
    <NuxtLink to="/auth/login">Войти</NuxtLink>
    <div>{{ user }}</div>
  </div>
</template>
