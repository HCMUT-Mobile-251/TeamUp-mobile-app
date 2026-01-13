/**
 * Normalize group member status values
 * Handles both Vietnamese and English status values from API
 * @param {string} status - Raw status value
 * @returns {string} Normalized status value
 */
export const normalizeStatus = (status) => {
  if (status === "Đã tham gia!" || status === "JOINED") return "JOINED";
  if (
    status === "Chờ được chấp nhận!" ||
    status === "WAITING_APPROVAL" ||
    status === "PENDING_APPROVAL"
  ) {
    return "PENDING";
  }
  if (status === "LEFT") return "LEFT";
  if (status === "REJECTED") return "REJECTED";
  return status;
};

/**
 * Get display text for status
 * @param {string} status - Normalized status value
 * @returns {string} User-friendly status text
 */
export const getStatusText = (status) => {
  const normalized = normalizeStatus(status);
  switch (normalized) {
    case "JOINED":
      return "Đã tham gia";
    case "PENDING":
      return "Chờ được chấp nhận";
    case "LEFT":
      return "Đã rời nhóm";
    case "REJECTED":
      return "Bị từ chối";
    default:
      return status;
  }
};

/**
 * Get status color
 * @param {string} status - Normalized status value
 * @returns {string} Color hex code
 */
export const getStatusColor = (status) => {
  const normalized = normalizeStatus(status);
  switch (normalized) {
    case "JOINED":
      return "#10B981"; // green
    case "PENDING":
      return "#F59E0B"; // orange
    case "LEFT":
      return "#6B7280"; // gray
    case "REJECTED":
      return "#EF4444"; // red
    default:
      return "#6B7280";
  }
};
