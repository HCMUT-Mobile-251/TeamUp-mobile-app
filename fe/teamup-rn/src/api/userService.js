import client from "./client";

/**
 * User Service - User management APIs
 */

/**
 * Get user by ID
 * @param {string} userId - User ID
 * @returns {Promise} - User data
 */
export const getUserById = async (userId) => {
  const response = await client.get(`/user/${userId}`);
  return response.data;
};

/**
 * Get user by student ID
 * @param {string} studentId - Student ID
 * @returns {Promise} - User data
 */
export const getUserByStudentId = async (studentId) => {
  const response = await client.get("/user", {
    params: { studentId },
  });
  return response.data;
};

/**
 * Update user information
 * @param {string} userId - User ID
 * @param {Object} data - User data to update
 * @param {string} data.faculty - Faculty name
 * @param {string} data.phoneNumber - Phone number
 * @param {string} data.studentId - Student ID
 * @returns {Promise} - Updated user data
 */
export const updateUser = async (userId, data) => {
  const response = await client.patch(`/user/${userId}`, data);
  return response.data;
};

/**
 * Delete user
 * @param {string} userId - User ID
 * @returns {Promise} - Delete response
 */
export const deleteUser = async (userId) => {
  const response = await client.delete(`/user/${userId}`);
  return response.data;
};

/**
 * Create admin user
 * @param {Object} data - User data
 * @param {string} data.firstName - First name
 * @param {string} data.lastName - Last name
 * @param {string} data.email - Email
 * @returns {Promise} - Created user data
 */
export const createAdminUser = async (data) => {
  const response = await client.post("/user/admin", data);
  return response.data;
};

/**
 * Get all users (admin only)
 * @returns {Promise} - List of all users
 */
export const getAllUsers = async () => {
  const response = await client.get("/user/admin/all");
  return response.data;
};

/**
 * Update user tags
 * @param {string} userId - User ID
 * @param {Array} tags - Array of tag objects [{tagId, name}]
 * @returns {Promise} - Updated user tags
 */
export const updateUserTags = async (userId, tags) => {
  const response = await client.patch(`/user/${userId}/tags`, tags);
  return response.data;
};
