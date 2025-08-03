import { useApiFetch } from "./useApiFetch";

export const useRecommendationApi = () => {
  const getRecommendations = async () => {
    const { data, error } = await useApiFetch("/recommendation");
    if (error.value) {
      throw new Error(`Ошибка загрузки рекомендаций: ${error.value.message}`);
    }

    return data.value;
  };

  return {
    getRecommendations,
  };
};
