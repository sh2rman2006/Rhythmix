import { useApiFetch } from "./useApiFetch";

export const useRecommendationApi = () => {
  const getRecommendations = async () => {
    const { data, error, refresh, status } = await useApiFetch(
      "/recommendation"
    );
    if (error.value) {
      throw new Error(`Ошибка загрузки рекомендаций: ${error.value.message}`);
    }

    return {
      error,
      data,
      status,
      refresh,
    };
  };

  return {
    getRecommendations,
  };
};
