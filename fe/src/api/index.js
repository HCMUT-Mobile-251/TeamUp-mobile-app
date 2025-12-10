/**
 * TeamUp API Services
 * Central export point for all API services
 */

// Export all services
export * as authService from "./authService";
export * as userService from "./userService";
export * as groupService from "./groupService";
export * as searchService from "./searchService";
export * as notificationService from "./notificationService";
export * as tagService from "./tagService";
export * as courseService from "./courseService";

// Export client for custom requests
export { default as client } from "./client";
