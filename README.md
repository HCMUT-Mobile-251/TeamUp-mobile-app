# 📚 TeamUp  

**TeamUp** là một ứng dụng hỗ trợ sinh viên **tìm / tạo nhóm** cho các **bài tập lớn (BTL), đồ án, tiểu luận**, dựa trên **tên hoặc mã môn học**.  
Ứng dụng giúp sinh viên kết nối nhanh chóng, tránh tình trạng “kẹt nhóm” khi làm việc nhóm ở trường.  

---

## 🚀 Tính năng chính
- 🔍 **Tìm nhóm** theo tên hoặc mã môn học.  
- 👥 **Tạo nhóm** mới cho môn học cần làm BTL/Đồ án.  
- 📌 **Quản lý thành viên** trong nhóm.  
- 💬 **Trao đổi thông tin** trong nhóm.  
- 🔐 **Đăng nhập bảo mật** với Spring Security.  

---

## 🛠️ Công nghệ sử dụng
### Backend
- **Spring Boot 3** (RESTful API)  
- **Spring Security + JWT** (xác thực, phân quyền)  
- **JPA / Hibernate** (làm việc với DB)  
- **MySQL** (cơ sở dữ liệu chính)  

### Mobile App
- **React Native + Expo** (phát triển ứng dụng đa nền tảng)  
- **Axios / Retrofit** (kết nối API)  
- **React Navigation** (điều hướng màn hình)  
- **TailwindCSS / NativeWind** (giao diện)  

---

## 📂 Cấu trúc dự án
- `be/` → Backend (Spring Boot)  
- `fe/` → Frontend Mobile App (Expo + React Native)  

---

## ⚡ Cách chạy
### Backend
```bash
cd be
./mvnw clean spring-boot:run

or simple 
mvn spring-boot:run
```

### Frontend
```bash
cd fe
pnpm install
pnpm start

or can be replace pnpm with npm or yarn
```


## 👨‍💻 Thành viên đóng góp
Nguyễn Huy Hoàng - 2211093
Nguyễn Thanh Hoàng - 2211101
Trương An Khang - 
Quách Hoàng - 