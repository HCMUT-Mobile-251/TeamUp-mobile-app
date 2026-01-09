import { useState, useCallback, useEffect, useRef } from "react";

/**
 * Custom hook for API calls with loading and error states
 * Includes AbortController support to prevent memory leaks
 * @param {Function} apiFunc - API function to call
 * @returns {Object} - { data, loading, error, execute, reset }
 */
export const useApi = (apiFunc) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const mountedRef = useRef(true);
  const abortControllerRef = useRef(null);

  useEffect(() => {
    // Track if component is mounted
    mountedRef.current = true;

    return () => {
      // Cleanup on unmount
      mountedRef.current = false;
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, []);

  const execute = useCallback(
    async (...args) => {
      // Cancel previous request if exists
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }

      // Create new AbortController for this request
      abortControllerRef.current = new AbortController();

      try {
        if (mountedRef.current) {
          setLoading(true);
          setError(null);
        }

        const result = await apiFunc(...args);

        if (mountedRef.current) {
          setData(result);
          setLoading(false);
        }
        return result;
      } catch (err) {
        // Don't update state if aborted or unmounted
        if (err.name === 'AbortError' || !mountedRef.current) {
          return;
        }

        const errorMessage = err.response?.data?.message || err.message || "Đã có lỗi xảy ra";
        if (mountedRef.current) {
          setError(errorMessage);
          setLoading(false);
        }
        throw err;
      }
    },
    [apiFunc]
  );

  const reset = useCallback(() => {
    if (mountedRef.current) {
      setData(null);
      setError(null);
      setLoading(false);
    }
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

  const refetch = useCallback(() => {
    return execute(...args);
  }, [execute, ...args]);

  // Auto-execute on mount using useEffect
  useEffect(() => {
    refetch();
  }, []); // Only run once on mount

  return { data, loading, error, refetch };
};
