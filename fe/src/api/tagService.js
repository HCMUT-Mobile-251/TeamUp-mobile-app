import client from "./client";

/**
 * Tag Service - Tag management APIs
 */

/**
 * Get all tags (public endpoint for all users)
 * @returns {Promise} - List of all tags
 */
export const getAllTags = async () => {
  const response = await client.get("/tag/all");
  return response.data;
};

/**
 * Search tags
 * @param {string} query - Search query
 * @returns {Promise} - Filtered tags
 */
export const searchTags = async (query) => {
  const response = await client.get("/tag", {
    params: { search: query },
  });
  return response.data;
};

/**
 * Create tags (admin)
 * @param {Array} tags - Array of tag objects [{name}]
 * @returns {Promise} - Created tags
 */
export const createTags = async (tags) => {
  const response = await client.post("/tag/admin", tags);
  return response.data;
};

/**
 * Delete tag (admin)
 * @param {string} tagId - Tag ID
 * @returns {Promise} - Delete response
 */
export const deleteTag = async (tagId) => {
  const response = await client.delete(`/tag/admin/${tagId}`);
  return response.data;
};

/**
 * Get tag suggestions for user
 * @param {string} userId - User ID
 * @returns {Promise} - Suggested tags (user's tags or random tags)
 */
export const getTagSuggestions = async (userId) => {
  const response = await client.get(`/tag/suggest/${userId}`);
  return response.data;
};

/**
 * Create a new tag (user)
 * @param {string} name - Tag name
 * @returns {Promise} - Created tag (or existing tag if duplicate)
 */
export const createTag = async (name) => {
  const response = await client.post("/tag", { name });
  return response.data;
};
