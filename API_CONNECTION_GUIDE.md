# Hướng dẫn kết nối API - TeamUp

## ✅ Đã hoàn thành

### 1. Cấu hình Backend
- **URL**: `http://localhost:8080`
- **Database**: AWS RDS MySQL 8.0.43
- **Status**: ✅ Đang chạy

### 2. Cấu hình Frontend
- **Base URL**: `http://localhost:8080` (từ `.env`)
- **Platform**: Web và Mobile support
- **Status**: ✅ Đang chạy

### 3. API Client
File: [`fe/src/api/client.js`](fe/src/api/client.js)

**Features**:
- ✅ Auto inject Bearer token vào headers
- ✅ Hỗ trợ cả Web (localStorage) và Mobile (SecureStore)
- ✅ Axios interceptor để xử lý authentication

```javascript
// Sử dụng
import client from './src/api/client';

const response = await client.get('/endpoint');
```

### 4. API Services có sẵn

#### Authentication (`authService.js`)
- `loginWithGoogle(code, scope, authuser, hd, prompt)` - Login với Google OAuth
- `getUserRole(token)` - Lấy role từ token

#### Course (`courseService.js`)
- `getAllCourses()` - Lấy tất cả courses (admin)
- `searchCourses(query)` - Tìm kiếm course theo ID hoặc tên
- `createCourses(courses)` - Tạo courses mới (admin)

#### Group (`groupService.js`)
- `getAllGroups()` - Lấy tất cả groups
- `getGroupById(id)` - Lấy group theo ID
- `getMyGroups()` - Lấy groups của user hiện tại
- `createGroup(data)` - Tạo group mới
- `updateGroup(id, data)` - Cập nhật group
- `deleteGroup(id)` - Xóa group
- `joinGroup(id)` - Tham gia group
- `leaveGroup(id)` - Rời group
- `approveJoinRequest(groupId, userId)` - Duyệt yêu cầu tham gia
- `rejectJoinRequest(groupId, userId)` - Từ chối yêu cầu

#### User (`userService.js`)
- `getUserProfile()` - Lấy profile user hiện tại
- `updateUserProfile(data)` - Cập nhật profile
- `getUserById(id)` - Lấy user theo ID
- `searchUsers(query)` - Tìm kiếm users

#### Notification (`notificationService.js`)
- `getAllNotifications()` - Lấy tất cả thông báo
- `markAsRead(id)` - Đánh dấu đã đọc
- `deleteNotification(id)` - Xóa thông báo

#### Search (`searchService.js`)
- `searchGroups(query)` - Tìm kiếm groups
- `advancedSearch(filters)` - Tìm kiếm nâng cao với filters

#### Tag (`tagService.js`)
- `getAllTags()` - Lấy tất cả tags
- `createTag(name)` - Tạo tag mới
- `deleteTag(id)` - Xóa tag

## 📝 Cách sử dụng

### 1. Import service cần dùng

```javascript
import { searchCourses } from './src/api/courseService';
import { getAllGroups } from './src/api/groupService';
```

### 2. Gọi API trong component

```javascript
const handleSearch = async () => {
  try {
    const courses = await searchCourses("vlkt");
    console.log(courses);
  } catch (error) {
    console.error("API Error:", error);
  }
};
```

### 3. Authentication

#### Trên Web
Token được lưu trong `localStorage`:
```javascript
localStorage.setItem("auth_token", "your-token");
```

#### Trên Mobile
Token được lưu trong `SecureStore`:
```javascript
import * as SecureStore from "expo-secure-store";
await SecureStore.setItemAsync("auth_token", "your-token");
```

## 🧪 Test API

Sử dụng screen test đã tạo: [`fe/screens/TestAPIScreen.js`](fe/screens/TestAPIScreen.js)

## 🔒 Security Notes

1. **NEVER** commit file `.env` vào git
2. Token được auto inject vào mọi request
3. Web platform dùng localStorage (ít an toàn hơn SecureStore)
4. Production nên dùng HTTPS

## 📚 Postman Collections

Các file Postman collection có sẵn:
- `course.postman_collection.json`
- `group.postman_collection.json`
- `login.postman_collection.json`
- `notification.postman_collection.json`
- `search.postman_collection.json`
- `tag.postman_collection.json`
- `user.postman_collection.json`

Import vào Postman để test API trực tiếp.

## ⚡ Troubleshooting

### Lỗi 401 Unauthorized
- Kiểm tra token có được set chưa
- Kiểm tra token còn hạn không
- Login lại để lấy token mới

### Lỗi Cannot connect
- Kiểm tra backend có đang chạy không: `lsof -i :8080`
- Kiểm tra URL trong `.env` file
- Kiểm tra network/firewall

### Lỗi CORS (trên web)
- Backend cần enable CORS cho localhost
- Kiểm tra Spring Security config
