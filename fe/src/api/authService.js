import client from "./client";

/**
 * Auth Service - Authentication APIs
 */

/**
 * Login with Google OAuth
 * @param {string} code - OAuth code from Google
 * @param {string} scope - OAuth scope
 * @param {string} authuser - Auth user
 * @param {string} hd - Host domain
 * @param {string} prompt - Prompt type
 * @returns {Promise} - Token response
 */
export const loginWithGoogle = async (code, scope, authuser, hd, prompt) => {
  const response = await client.get("/auth/login", {
    params: { code, scope, authuser, hd, prompt },
  });
  return response.data;
};

/**
 * Get user role from token
 * @param {string} token - Auth token
 * @returns {Promise} - Role information
 */
export const getUserRole = async (token) => {
  const response = await client.get(`/auth/${token}`);
  return response.data;
};
