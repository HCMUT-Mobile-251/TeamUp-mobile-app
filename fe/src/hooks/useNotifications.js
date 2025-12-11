import { useApiOnMount, useApi } from "./useApi";
import { notificationService } from "../api";

/**
 * Hook to get notifications by user ID
 */
export const useNotifications = (userId) => {
  return useApiOnMount(() => notificationService.getNotificationsByUserId(userId));
};

/**
 * Hook to search notifications
 */
export const useSearchNotifications = () => {
  return useApi(notificationService.searchNotifications);
};

/**
 * Hook to delete notification
 */
export const useDeleteNotification = () => {
  return useApi(notificationService.deleteNotification);
};
