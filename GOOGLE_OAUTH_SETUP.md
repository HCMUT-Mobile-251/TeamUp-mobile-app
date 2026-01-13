# Hướng dẫn cấu hình Google OAuth cho TeamUp

## Bước 1: Truy cập Google Cloud Console

1. Mở [Google Cloud Console](https://console.cloud.google.com/)
2. Chọn project `TeamUp` (hoặc project đang sử dụng)

## Bước 2: Cấu hình OAuth Consent Screen

1. Vào menu **APIs & Services** > **OAuth consent screen**
2. Chọn **Internal** (chỉ cho phép tài khoản @hcmut.edu.vn)
3. Điền thông tin:
   - App name: `TeamUp`
   - User support email: email của bạn
   - Developer contact: email của bạn
4. Nhấn **Save and Continue**

## Bước 3: Thêm Authorized Redirect URIs

1. Vào menu **APIs & Services** > **Credentials**
2. Chọn OAuth 2.0 Client ID hiện tại (Client ID: `67346913521-0bql06om6o8kj610ferhl52le2uqh3jr.apps.googleusercontent.com`)
3. Trong phần **Authorized redirect URIs**, thêm các URI sau:
   ```
   http://localhost:8080/auth-redirect.html
   http://localhost:8080/auth/login
   ```
4. Nhấn **Save**

## Bước 4: Kiểm tra cấu hình

### Backend (`be/application.properties`):
```properties
google.GOOGLE_CLIENT_ID=67346913521-0bql06om6o8kj610ferhl52le2uqh3jr.apps.googleusercontent.com
google.GOOGLE_CLIENT_SECRET=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
google.GOOGLE_REDIRECT_URI=http://localhost:8080/auth-redirect.html
```

### Frontend (`fe/screens/LoginScreen.js`):
OAuth URL phải chứa:
```
redirect_uri=http://localhost:8080/auth-redirect.html
```

## Luồng đăng nhập

1. User nhấn "Sign in with Google" trong app
2. App mở browser với URL OAuth của Google
3. User đăng nhập bằng tài khoản @hcmut.edu.vn
4. Google redirect về `http://localhost:8080/auth-redirect.html?code=...`
5. File `auth-redirect.html` gọi API `/auth/login?code=...` để lấy token
6. Page redirect về app với deep link: `teamup://auth?token=...&userId=...`
7. App nhận deep link, lưu token và userId, navigate đến Home screen

## Troubleshooting

### Lỗi "redirect_uri_mismatch"
- Kiểm tra xem `http://localhost:8080/auth-redirect.html` đã được thêm vào **Authorized redirect URIs** trong Google Cloud Console chưa
- Đảm bảo URI trong code khớp chính xác (không có dấu `/` thừa ở cuối)

### Sau login vẫn hiển thị onboarding screen
- Kiểm tra xem deep link `teamup://` đã được đăng ký trong `app.json` chưa
- Kiểm tra console log để xem có nhận được token và userId không

### Token không hợp lệ
- Token từ Google OAuth có thời hạn ngắn (thường 1 giờ)
- Nếu cần token lâu dài hơn, cần implement refresh token flow
