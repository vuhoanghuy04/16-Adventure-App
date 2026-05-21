# Hướng dẫn cài đặt và chạy ứng dụng (Dành cho Giám khảo/Giảng viên)

Dự án này sử dụng các dịch vụ của Google (Firebase, Gemini AI, Google Maps). Để bảo mật, các thông tin cấu hình nhạy cảm đã được loại bỏ khỏi kho lưu trữ công khai. Để chạy được ứng dụng, vui lòng thực hiện các bước sau:

### 1. Cấu hình API Keys (Gemini & Google Maps)
1. Tại thư mục gốc của dự án, tìm file `local.properties.example`.
2. Tạo một bản sao và đổi tên thành `local.properties`.
3. Mở file `local.properties` và điền các khóa API của bạn vào:
   ```properties
   GEMINI_API_KEY=AIzaSy... (Khóa lấy từ Google AI Studio)
   MAPS_API_KEY=AIzaSy...   (Khóa lấy từ Google Cloud Console)
   ```

### 2. Cấu hình Firebase
Ứng dụng sử dụng Firebase cho chức năng Đăng nhập, Firestore và Storage.
1. Truy cập [Firebase Console](https://console.firebase.google.com/).
2. Tạo một dự án mới và thêm ứng dụng Android với Package Name: `com.example.a16adventure`.
3. Tải file cấu hình `google-services.json`.
4. Chép file `google-services.json` vào thư mục `/app` của dự án.
5. Đảm bảo bạn đã bật các dịch vụ sau trong Firebase Console:
   - Authentication (Email/Password)
   - Realtime Database
   - Cloud Firestore
   - Storage

### 3. Build dự án
1. Mở dự án bằng Android Studio.
2. Thực hiện **Sync Project with Gradle Files**.
3. Chạy ứng dụng trên thiết bị ảo hoặc thiết bị thật (API 24 trở lên).

---
*Lưu ý: Nếu không thực hiện các bước trên, ứng dụng sẽ gặp lỗi khi build hoặc không thể sử dụng các tính năng liên quan đến AI và Bản đồ.*
