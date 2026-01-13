import client from "./client";

/**
 * Notification Service - Notification management APIs
 */

/**
 * Get notifications by user ID
 * @param {string} userId - User ID
 * @returns {Promise} - List of notifications
 */
export const getNotificationsByUserId = async (userId) => {
  const response = await client.get(`/notification/${userId}`);
  return response.data;
};

/**
 * Search notifications
 * @param {string} userId - User ID
 * @param {string} query - Search query
 * @returns {Promise} - Filtered notifications
 */
export const searchNotifications = async (userId, query) => {
  const response = await client.get(`/notification/${userId}/search`, {
    params: { search: query },
  });
  return response.data;
};

/**
 * Delete notification
 * @param {Object} data - Notification IDs
 * @param {string} data.firstId - First ID (user or group)
 * @param {string} data.secondId - Second ID (user or group)
 * @returns {Promise} - Delete response
 */
export const deleteNotification = async (data) => {
  const response = await client.delete("/notification/delete", {
    data,
  });
  return response.data;
};
