import { useApi, useApiOnMount } from "./useApi";
import { userService } from "../api";

/**
 * Hook to get user by ID
 */
export const useUser = (userId) => {
  return useApiOnMount(() => userService.getUserById(userId));
};

/**
 * Hook to update user
 */
export const useUpdateUser = () => {
  return useApi(userService.updateUser);
};

/**
 * Hook to update user tags
 */
export const useUpdateUserTags = () => {
  return useApi(userService.updateUserTags);
};
