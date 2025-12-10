import { useApi } from "./useApi";
import { searchService } from "../api";

/**
 * Hook for normal search
 */
export const useNormalSearch = () => {
  return useApi(searchService.searchNormal);
};

/**
 * Hook for advanced search
 */
export const useAdvancedSearch = () => {
  return useApi(searchService.searchAdvanced);
};
