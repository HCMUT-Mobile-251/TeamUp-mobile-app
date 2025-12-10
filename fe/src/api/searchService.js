import client from "./client";

/**
 * Search Service - Search APIs
 */

/**
 * Normal search for groups
 * @param {string} query - Search query
 * @param {string} userId - User ID
 * @returns {Promise} - Search results
 */
export const searchNormal = async (query, userId) => {
  const response = await client.get("/search/normal", {
    params: {
      group: query,
      userId,
    },
  });
  return response.data;
};

/**
 * Advanced search with multiple criteria
 * @param {Object} criteria - Search criteria
 * @param {string} criteria.name - Group name
 * @param {string} criteria.groupClass - Group class
 * @param {string} criteria.topicName - Topic name
 * @param {Array<string>} criteria.tagId - Array of tag IDs
 * @param {Object} criteria.course - Course object
 * @param {string} criteria.course.courseId - Course ID
 * @param {string} criteria.course.name - Course name
 * @param {string} criteria.userId - User ID
 * @returns {Promise} - Advanced search results
 */
export const searchAdvanced = async (criteria) => {
  const response = await client.get("/search/advance", {
    data: criteria,
  });
  return response.data;
};
