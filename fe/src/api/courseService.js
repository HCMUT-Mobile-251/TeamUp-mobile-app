import client from "./client";

/**
 * Course Service - Course management APIs
 */

/**
 * Get all courses (admin)
 * @returns {Promise} - List of all courses
 */
export const getAllCourses = async () => {
  const response = await client.get("/course/admin/all");
  return response.data;
};

/**
 * Search courses by ID or name
 * @param {string} query - Search query
 * @returns {Promise} - Filtered courses
 */
export const searchCourses = async (query) => {
  const response = await client.get("/course", {
    params: { search: query },
  });
  return response.data;
};

/**
 * Create courses (admin)
 * @param {Array} courses - Array of course objects [{courseId, name}]
 * @returns {Promise} - Created courses
 */
export const createCourses = async (courses) => {
  const response = await client.post("/course/admin", courses);
  return response.data;
};
