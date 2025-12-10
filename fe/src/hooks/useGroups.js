import { useApi, useApiOnMount } from "./useApi";
import { groupService } from "../api";

/**
 * Hook to get all groups
 */
export const useAllGroups = (page = 0, size = 20) => {
  return useApiOnMount(() => groupService.getAllGroups(page, size));
};

/**
 * Hook to get suggested groups
 */
export const useSuggestedGroups = (userId, page = 0, size = 20) => {
  return useApiOnMount(() => groupService.getSuggestedGroups(userId, page, size));
};

/**
 * Hook to get group by ID
 */
export const useGroup = (groupId) => {
  return useApiOnMount(() => groupService.getGroupById(groupId));
};

/**
 * Hook to create group
 */
export const useCreateGroup = () => {
  return useApi(groupService.createGroup);
};

/**
 * Hook to send join request
 */
export const useJoinGroup = () => {
  return useApi(groupService.sendJoinRequest);
};

/**
 * Hook to accept join request
 */
export const useAcceptJoinRequest = () => {
  return useApi(groupService.acceptJoinRequest);
};

/**
 * Hook to reject join request
 */
export const useRejectJoinRequest = () => {
  return useApi(groupService.rejectJoinRequest);
};
