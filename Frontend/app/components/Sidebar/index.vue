<script setup lang="ts">
import { useRoute } from "vue-router";
import { onMounted, ref } from "vue";

const route = useRoute();
const isMounted = ref(false);

const navItems = [
  { label: "Поиск", to: "/search", icon: "lucide:search" },
  { label: "Главная", to: "/", icon: "lucide:home" },
  { label: "Параметры", to: "/params", icon: "lucide:sliders" },
  { label: "Количество", to: "/quantity", icon: "lucide:bar-chart" },
  { label: "Избранное", to: "/favorites", icon: "lucide:heart" },
];

onMounted(() => {
  isMounted.value = true;
});
</script>

<template>
  <aside
    class="fixed top-0 left-0 h-screen w-64 bg-black border-r border-gray-800 p-4 flex flex-col justify-between transition-opacity duration-500"
    :class="isMounted ? 'opacity-100' : 'opacity-0'"
  >
    <!-- Логотип -->
    <div class="mb-8">
      <NuxtLink to="/" class="text-lime-400 font-bold text-2xl tracking-tight flex items-center gap-2">
        <Icon name="lucide:music" class="w-7 h-7" />
        <span>Rhythmix</span>
      </NuxtLink>
      <p class="text-xs text-gray-500 mt-2 tracking-wide">Самые точные рекомендации</p>
    </div>

    <!-- Навигация -->
    <nav class="space-y-1 flex-1">
      <NuxtLink
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="flex items-center gap-3 px-3 py-3 text-gray-300 rounded-lg transition-all group"
        :class="{
          'bg-gray-800 text-white': isMounted && route.path === item.to,
          'hover:bg-gray-800/50': route.path !== item.to
        }"
      >
        <Icon
          :name="item.icon"
          class="w-5 h-5 transition-colors"
          :class="isMounted && route.path === item.to 
            ? 'text-lime-400' 
            : 'text-gray-400 group-hover:text-lime-300'"
        />
        <span class="font-medium tracking-wide">{{ item.label }}</span>
      </NuxtLink>
    </nav>

    <!-- Пользователь -->
    <div
      class="flex items-center gap-3 p-3 mt-4 rounded-lg bg-gray-800/30 transition hover:bg-gray-800/50"
    >
      <div class="bg-gray-700 rounded-full p-1">
        <Icon name="lucide:user-circle" class="w-7 h-7 text-lime-400" />
      </div>
      <div>
        <p class="text-sm text-gray-100 font-medium">Константин М.</p>
        <span class="text-xs text-pink-400 font-semibold flex items-center gap-1">
          <Icon name="lucide:sparkles" class="w-3 h-3" /> Плюс
        </span>
      </div>
    </div>
  </aside>
</template>