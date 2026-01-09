import client from "./client";

/**
 * Group Service - Group management APIs
 */

/**
 * Get all groups with pagination (admin)
 * @param {number} page - Page number (default 0)
 * @param {number} size - Page size (default 20)
 * @returns {Promise} - Paginated groups list
 */
export const getAllGroups = async (page = 0, size = 20) => {
  const response = await client.get("/group/admin/all", {
    params: { page, size },
  });
  return response.data;
};

/**
 * Get group by ID
 * @param {string} groupId - Group ID
 * @returns {Promise} - Group data
 */
export const getGroupById = async (groupId) => {
  const response = await client.get(`/group/${groupId}`);
  return response.data;
};

/**
 * Get group members
 * @param {string} groupId - Group ID
 * @returns {Promise} - List of group members
 */
export const getGroupMembers = async (groupId) => {
  const response = await client.get(`/group/${groupId}/members`);
  return response.data;
};

/**
 * Get suggested groups for user
 * @param {string} userId - User ID
 * @param {number} page - Page number (default 0)
 * @param {number} size - Page size (default 20)
 * @returns {Promise} - Suggested groups list
 */
export const getSuggestedGroups = async (userId, page = 0, size = 20) => {
  const response = await client.get(`/group/suggest/${userId}`, {
    params: { page, size },
  });
  return response.data;
};

/**
 * Create a new group
 * @param {Object} data - Group data
 * @param {string} data.name - Group name
 * @param {string} data.description - Group description
 * @param {string} data.groupClass - Group class
 * @param {string} data.topicName - Topic name
 * @param {number} data.maxMembers - Maximum members
 * @param {string} data.leaderId - Leader user ID
 * @param {string} data.courseId - Course ID
 * @returns {Promise} - Created group data
 */
export const createGroup = async (data) => {
  const response = await client.post("/group", data);
  return response.data;
};

/**
 * Update group information
 * @param {string} groupId - Group ID
 * @param {Object} data - Group data to update
 * @returns {Promise} - Updated group data
 */
export const updateGroup = async (groupId, data) => {
  const response = await client.patch(`/group/${groupId}`, data);
  return response.data;
};

/**
 * Delete group
 * @param {string} groupId - Group ID
 * @returns {Promise} - Delete response
 */
export const deleteGroup = async (groupId) => {
  const response = await client.delete(`/group/${groupId}`);
  return response.data;
};

/**
 * Send join request to group
 * @param {string} groupId - Group ID
 * @param {Object} data - Request data
 * @param {string} data.userId - User ID
 * @param {string} data.message - Join request message
 * @returns {Promise} - Join request response
 */
export const sendJoinRequest = async (groupId, data) => {
  const response = await client.patch(`/group/${groupId}/join`, data);
  return response.data;
};

/**
 * Accept join request
 * @param {string} groupId - Group ID
 * @param {string} userId - User ID to accept
 * @returns {Promise} - Accept response
 */
export const acceptJoinRequest = async (groupId, userId) => {
  const response = await client.patch(`/group/${groupId}/accept`, null, {
    params: { userId },
  });
  return response.data;
};

/**
 * Reject join request
 * @param {string} groupId - Group ID
 * @param {string} userId - User ID to reject
 * @returns {Promise} - Reject response
 */
export const rejectJoinRequest = async (groupId, userId) => {
  const response = await client.patch(`/group/${groupId}/reject`, null, {
    params: { userId },
  });
  return response.data;
};

/**
 * Leave group
 * @param {string} groupId - Group ID
 * @param {string} userId - User ID
 * @returns {Promise} - Leave response
 */
export const leaveGroup = async (groupId, userId) => {
  const response = await client.patch(`/group/${groupId}/out`, null, {
    params: { userId },
  });
  return response.data;
};

/**
 * Increase group members
 * @param {string} groupId - Group ID
 * @param {Array<string>} userIds - Array of user IDs to add
 * @returns {Promise} - Update response
 */
export const increaseMembers = async (groupId, userIds) => {
  const response = await client.patch(`/group/${groupId}/increase`, userIds);
  return response.data;
};

/**
 * Decrease group members (remove member)
 * @param {string} groupId - Group ID
 * @param {string} userId - User ID to remove
 * @returns {Promise} - Update response
 */
export const decreaseMember = async (groupId, userId) => {
  const response = await client.patch(`/group/${groupId}/decrease`, null, {
    params: { userId },
  });
  return response.data;
};

/**
 * Update group tags
 * @param {string} groupId - Group ID
 * @param {Array} tags - Array of tag objects [{tagId, name}]
 * @returns {Promise} - Updated group tags
 */
export const updateGroupTags = async (groupId, tags) => {
  const response = await client.patch(`/group/${groupId}/tags`, tags);
  return response.data;
};

/**
 * Invite member to group by MSSV or Email
 * @param {string} groupId - Group ID
 * @param {string} identifier - MSSV or Email
 * @returns {Promise} - Invite response
 */
export const inviteMemberByIdentifier = async (groupId, identifier) => {
  const response = await client.patch(`/group/${groupId}/invite`, {
    identifier,
  });
  return response.data;
};

/**
 * Transfer leadership to another member
 * @param {string} groupId - Group ID
 * @param {string} newLeaderId - New leader's user ID
 * @param {string} courseId - Course ID (required by backend)
 * @returns {Promise} - Transfer response
 */
export const transferLeadership = async (groupId, newLeaderId, courseId) => {
  const response = await client.patch(`/group/${groupId}`, {
    groupId,
    leaderId: newLeaderId,
    courseId,
  });
  return response.data;
};
