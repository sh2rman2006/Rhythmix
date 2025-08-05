import { useApiFetch } from "./useApiFetch";
import { useClientFetch } from "./useClientFetch";
import type { RawUserDto } from "~/types/dto/UserDto";
import { UserDto } from "~/types/dto/UserDto";

export const useUserApi = () => {
  const getMeSSR = async (): Promise<UserDto> => {
    const { data, error } = await useApiFetch<RawUserDto>("/user/me", {
      method: "GET",
    });
    if (error.value) {
      throw new Error(`Ошибка загрузки пользователя: ${error.value.message}`);
    }
    return UserDto.fromRaw(data.value);
  };

  const getMeClient = async (): Promise<UserDto> => {
    const rawData = await useClientFetch<RawUserDto>("/user/me");
    return UserDto.fromRaw(rawData);
  };

  return {
    getMeSSR,
    getMeClient,
  };
};
