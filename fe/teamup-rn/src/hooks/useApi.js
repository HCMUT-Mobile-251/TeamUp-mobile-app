import { useState, useCallback } from "react";

/**
 * Custom hook for API calls with loading and error states
 * @param {Function} apiFunc - API function to call
 * @returns {Object} - { data, loading, error, execute, reset }
 */
export const useApi = (apiFunc) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = useCallback(
    async (...args) => {
      try {
        setLoading(true);
        setError(null);
        const result = await apiFunc(...args);
        setData(result);
        return result;
      } catch (err) {
        const errorMessage = err.response?.data?.message || err.message || "Đã có lỗi xảy ra";
        setError(errorMessage);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [apiFunc]
  );

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setLoading(false);
  }, []);

  return { data, loading, error, execute, reset };
};

/**
 * Custom hook for API calls that auto-execute on mount
 * @param {Function} apiFunc - API function to call
 * @param {Array} deps - Dependencies array for useEffect
 * @returns {Object} - { data, loading, error, refetch }
 */
export const useApiOnMount = (apiFunc, ...args) => {
  const { data, loading, error, execute } = useApi(apiFunc);
  const [initialized, setInitialized] = useState(false);

  const refetch = useCallback(() => {
    return execute(...args);
  }, [execute, args]);

  // Auto-execute on mount
  if (!initialized && !loading) {
    setInitialized(true);
    refetch();
  }

  return { data, loading, error, refetch };
};
