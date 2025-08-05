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

const isLoading = ref(false);
const errorMessage = ref("");

async function handleLogin() {
  isLoading.value = true;
  errorMessage.value = "";

  try {
    await authStore.login(form.email, form.password);
  } catch (error: any) {
    errorMessage.value = error?.message || "Ошибка входа";
  } finally {
    isLoading.value = false;
  }
}
</script>

<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-950 via-black to-gray-900"
  >
    <div
      class="w-full max-w-sm p-8 rounded-2xl bg-gray-900/80 backdrop-blur border border-gray-800 shadow-xl space-y-6"
    >
      <div class="text-center space-y-2">
        <h1 class="text-3xl font-bold text-white">Добро пожаловать</h1>
        <p class="text-sm text-gray-400">Войдите, чтобы слушать музыку</p>
      </div>

      <form class="space-y-4" @submit.prevent="handleLogin">
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

        <div v-if="errorMessage" class="text-sm text-red-500">
          {{ errorMessage }}
        </div>

        <UButton
          type="submit"
          variant="soft"
          color="neutral"
          class="w-full flex justify-center"
          :loading="isLoading"
        >
          Войти
        </UButton>
      </form>
    </div>
  </div>
</template>
