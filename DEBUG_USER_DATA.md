# 🔍 Hướng dẫn Debug - Vấn đề tải thông tin người dùng

## Các vấn đề đã được fix:

### ✅ 1. Response Interceptor API (`fe/src/api/client.js`)
- **Vấn đề**: Không có xử lý error response từ API
- **Fix**: Thêm `response.interceptor` để log chi tiết lỗi 401, 403, 500, etc.

### ✅ 2. Error Handling trong ProfileScreen (`fe/screens/ProfileScreen.js`)
- **Vấn đề**: Chỉ catch network error, không handle HTTP error status codes
- **Fix**: 
  - Thêm kiểm tra `response?.code === 200` an toàn
  - Xử lý cụ thể cho các status codes: 401 (hết hạn), 403 (không quyền), 404 (không tìm thấy)
  - Thêm check `userId` xem có tồn tại không

### ✅ 3. Debug logs
- **Thêm**: Console logs trong `loadUserData()` để dễ track flow
- **Thêm**: Dependency `userId` trong useEffect

---

## 📱 Các bước để debug lỗi:

### **Bước 1: Kiểm tra token và userId**
Mở DevTools Console (F12 trên web) và xem:
```
[App.js] Reading from localStorage: { token: "...", userId: "...", onboardingSeen: ... }
[ProfileScreen] userId from context: <userId>
```

### **Bước 2: Kiểm tra API request**
Mở Network tab → tìm request tới `/user/{userId}`
```
GET http://localhost:8080/user/af4937ad-0d3b-4bfe-ba61-ba984f266c48
Headers:
  Authorization: Bearer <token>
```

### **Bước 3: Kiểm tra API response**
Xem response từ Backend:
```json
{
  "code": 200,
  "message": "Lấy người dùng thành công",
  "result": {
    "userId": "af4937ad-0d3b-4bfe-ba61-ba984f266c48",
    "fullName": "...",
    "email": "...",
    "studentId": "...",
    "faculty": "...",
    "phoneNumber": "...",
    "userTags": [...],
    "groups": [...]
  }
}
```

### **Bước 4: Kiểm tra Console logs**
Tìm các dòng:
```
[ProfileScreen] loadUserData called, userId: af4937ad-0d3b-4bfe-ba61-ba984f266c48
[ProfileScreen] User Data Response: { code: 200, result: {...} }
```

---

## 🔧 Các tình huống lỗi phổ biến:

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-----------|----------|
| `userId = null` | Chưa đăng nhập hoặc localStorage/SecureStore bị xóa | Đăng nhập lại |
| `401 Unauthorized` | Token hết hạn | Refresh token hoặc đăng nhập lại |
| `403 Forbidden` | Không có quyền truy cập | Kiểm tra role của user |
| `404 Not Found` | userId không tồn tại trong DB | Kiểm tra DB có data không |
| `Network Error` | Backend không chạy hoặc API_BASE_URL sai | Kiểm tra backend & config |

---

## 🚀 Để test nhanh:

### **Với SKIP_AUTH** (nếu cấu hình trong app.json):
```json
{
  "extra": {
    "SKIP_AUTH": true,
    "API_BASE_URL": "http://localhost:8080"
  }
}
```
→ App sẽ bypass login và dùng test userId: `af4937ad-0d3b-4bfe-ba61-ba984f266c48`

### **Chạy Backend**:
```bash
cd be
./mvnw spring-boot:run
```

### **Chạy Frontend**:
```bash
cd fe
npm start
```

---

## 📝 Các file đã sửa:

1. **`fe/src/api/client.js`** - Thêm response interceptor
2. **`fe/screens/ProfileScreen.js`** - Cải thiện error handling & debug logs

---

## 💡 Tips:

- Nếu vẫn không load được, kiểm tra **DevTools Console** (F12) để xem exact error message
- Kiểm tra **Network tab** xem API request/response như thế nào
- Nếu backend không chạy, bạn sẽ thấy `Network Error` ngay
- Nếu token sai, bạn sẽ thấy `401 Unauthorized`

