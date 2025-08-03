<script setup lang="ts">
type Form = {
  email: string;
  password: string;
};

const colorMode = useColorMode();
colorMode.preference = `dark`;
const isDark = computed((): boolean => colorMode.preference === "dark");

const authStore = useAuthStore();
const form = reactive<Form>({
  email: "",
  password: "",
});
</script>

<template>
  <div :class="{ 'dark-page': isDark }">
    <h1>Привет, Nuxt и Tailwind!</h1>
    <form
      @submit.prevent="authStore.login(form.email, form.password)"
      style="display: flex; flex-direction: column"
    >
      <input v-model="form.email" type="text" placeholder="email" />
      <input v-model="form.password" type="text" placeholder="password" />
      <button type="submit">отправить</button>
    </form>
  </div>
</template>
