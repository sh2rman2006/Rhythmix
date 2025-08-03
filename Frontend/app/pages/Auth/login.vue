<script setup lang="ts">
type Form = {
  email: string;
  password: string;
};

const authStore = useAuthStore();

const form = reactive<Form>({
  email: "",
  password: "",
});
</script>

<template>
  <div class="dark-page flex items-center justify-center min-h-screen">
    <form
      class="w-full max-w-sm p-8 rounded-2xl bg-gray-900/80 backdrop-blur border border-gray-800 shadow-xl space-y-5"
      @submit.prevent="authStore.login(form.email, form.password)"
    >
      <h1 class="text-2xl font-bold text-center text-white">Вход в систему</h1>

      <UInput
        v-model="form.email"
        variant="outline"
        color="neutral"
        placeholder="Email"
        class="w-full"
      />
      <UInput
        v-model="form.password"
        variant="outline"
        color="neutral"
        type="password"
        placeholder="Пароль"
        class="w-full"
      />

      <UButton
        type="submit"
        variant="outline"
        color="neutral"
        class="w-full flex justify-center"
      >
        Войти
      </UButton>
    </form>
  </div>
</template>

<style scoped>
.page__login {
  min-width: 100vw;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 30px;
  padding: 20px;
  border-radius: 16px;
}
</style>
