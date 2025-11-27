import client from "./client";

/**
 * Tag Service - Tag management APIs
 */

/**
 * Get all tags (admin)
 * @returns {Promise} - List of all tags
 */
export const getAllTags = async () => {
  const response = await client.get("/tag/admin/all");
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
