# TeamUp API Services

Tất cả API services cho TeamUp mobile app.

## 📁 Cấu trúc

```
src/api/
├── client.js              # Axios client với auth interceptor
├── index.js               # Export tất cả services
├── authService.js         # Authentication APIs
├── userService.js         # User management APIs
├── groupService.js        # Group management APIs
├── searchService.js       # Search APIs
├── notificationService.js # Notification APIs
├── tagService.js          # Tag management APIs
└── courseService.js       # Course management APIs
```

## 🚀 Cách sử dụng

### Import services

```javascript
import {
  authService,
  userService,
  groupService,
  searchService,
  notificationService,
  tagService,
  courseService,
} from "../src/api";
```

### Hoặc import từng service

```javascript
import { getUserById } from "../src/api/userService";
import { getAllGroups } from "../src/api/groupService";
```

## 📚 Ví dụ sử dụng

### 1. Authentication

```javascript
import { authService } from "../src/api";

// Login với Google OAuth
const loginUser = async (code, scope, authuser, hd, prompt) => {
  try {
    const data = await authService.loginWithGoogle(
      code,
      scope,
      authuser,
      hd,
      prompt
    );
    console.log("Token:", data);
  } catch (error) {
    console.error("Login failed:", error);
  }
};
```

### 2. User Management

```javascript
import { userService } from "../src/api";

// Lấy thông tin user
const fetchUser = async (userId) => {
  try {
    const user = await userService.getUserById(userId);
    console.log("User:", user);
  } catch (error) {
    console.error("Error:", error);
  }
};

// Update user
const updateUserInfo = async (userId, data) => {
  try {
    const updated = await userService.updateUser(userId, {
      faculty: "KHMT",
      phoneNumber: "0123456789",
      studentId: "2211111",
    });
    console.log("Updated:", updated);
  } catch (error) {
    console.error("Error:", error);
  }
};
```

### 3. Group Management

```javascript
import { groupService } from "../src/api";

// Lấy danh sách groups
const fetchGroups = async () => {
  try {
    const groups = await groupService.getAllGroups(0, 20);
    console.log("Groups:", groups);
  } catch (error) {
    console.error("Error:", error);
  }
};

// Tạo group mới
const createNewGroup = async () => {
  try {
    const group = await groupService.createGroup({
      name: "Group A",
      description: "Nhóm làm đồ án",
      groupClass: "L02",
      topicName: "Project A",
      maxMembers: 5,
      leaderId: "user-id-here",
      courseId: "AS2015",
    });
    console.log("Created group:", group);
  } catch (error) {
    console.error("Error:", error);
  }
};

// Gửi join request
const joinGroup = async (groupId) => {
  try {
    await groupService.sendJoinRequest(groupId, {
      userId: "user-id",
      message: "Tôi muốn tham gia nhóm",
    });
    console.log("Join request sent");
  } catch (error) {
    console.error("Error:", error);
  }
};

// Accept join request
const acceptMember = async (groupId, userId) => {
  try {
    await groupService.acceptJoinRequest(groupId, userId);
    console.log("Member accepted");
  } catch (error) {
    console.error("Error:", error);
  }
};
```

### 4. Search

```javascript
import { searchService } from "../src/api";

// Tìm kiếm thường
const searchGroups = async (query, userId) => {
  try {
    const results = await searchService.searchNormal(query, userId);
    console.log("Results:", results);
  } catch (error) {
    console.error("Error:", error);
  }
};

// Tìm kiếm nâng cao
const advancedSearch = async () => {
  try {
    const results = await searchService.searchAdvanced({
      name: "Group",
      groupClass: "L02",
      topicName: "Project",
      tagId: ["tag-id-1", "tag-id-2"],
      course: {
        courseId: "AS2015",
        name: "Thực tập",
      },
      userId: "user-id",
    });
    console.log("Results:", results);
  } catch (error) {
    console.error("Error:", error);
  }
};
```

### 5. Notifications

```javascript
import { notificationService } from "../src/api";

// Lấy notifications
const fetchNotifications = async (userId) => {
  try {
    const notifications = await notificationService.getNotificationsByUserId(
      userId
    );
    console.log("Notifications:", notifications);
  } catch (error) {
    console.error("Error:", error);
  }
};
```

### 6. Tags

```javascript
import { tagService } from "../src/api";

// Lấy tất cả tags
const fetchTags = async () => {
  try {
    const tags = await tagService.getAllTags();
    console.log("Tags:", tags);
  } catch (error) {
    console.error("Error:", error);
  }
};

// Tìm kiếm tags
const searchTagsFunc = async (query) => {
  try {
    const tags = await tagService.searchTags(query);
    console.log("Tags:", tags);
  } catch (error) {
    console.error("Error:", error);
  }
};
```

### 7. Courses

```javascript
import { courseService } from "../src/api";

// Lấy tất cả courses
const fetchCourses = async () => {
  try {
    const courses = await courseService.getAllCourses();
    console.log("Courses:", courses);
  } catch (error) {
    console.error("Error:", error);
  }
};

// Tìm kiếm courses
const searchCoursesFunc = async (query) => {
  try {
    const courses = await courseService.searchCourses(query);
    console.log("Courses:", courses);
  } catch (error) {
    console.error("Error:", error);
  }
};
```

## 🔧 Error Handling

Tất cả API calls đều có thể throw errors. Luôn sử dụng try-catch:

```javascript
import { groupService } from "../src/api";

const safeApiCall = async () => {
  try {
    const data = await groupService.getAllGroups();
    // Success
    return data;
  } catch (error) {
    // Error handling
    if (error.response) {
      // Server error
      console.error("Server error:", error.response.data);
      console.error("Status:", error.response.status);
    } else if (error.request) {
      // Network error
      console.error("Network error:", error.request);
    } else {
      // Other errors
      console.error("Error:", error.message);
    }
    throw error;
  }
};
```

## 🔐 Authentication

API client tự động thêm Bearer token vào mỗi request nếu có token trong SecureStore.

Token được lưu bởi AuthContext khi user login thành công.

## 📝 Notes

- Base URL: `http://localhost:8080` (có thể config trong app.json)
- Tất cả requests sử dụng Bearer token authentication
- Axios interceptor tự động attach token vào headers
- Pagination default: page=0, size=20
