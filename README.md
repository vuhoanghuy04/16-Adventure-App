# 16 Adventure App

Ứng dụng Android khám phá du lịch Hải Phòng (địa danh, bản đồ, quiz, AI chat, nhận diện ảnh).

## Yêu cầu môi trường

- Android Studio mới (khuyến nghị Hedgehog+)
- JDK 17
- Android SDK API 35

## Cấu hình nhanh

1. Tạo file `local.properties` ở thư mục gốc dự án.
2. Khai báo các key:

```properties
GEMINI_API_KEY=your_gemini_api_key
MAPS_API_KEY=your_google_maps_api_key
```

3. Đảm bảo có file `app/google-services.json`.
4. Sync Gradle và chạy app.

## Firebase checklist

- Bật các dịch vụ: Authentication, Realtime Database, Firestore, Storage.
- Cập nhật rule phù hợp cho môi trường dev/test.
- Kiểm tra các đường dẫn dữ liệu chính:
  - `users/{uid}/saved_ids`
  - `users/{uid}/quiz`
  - `users/{uid}/journals`

## Lệnh kiểm tra

```bash
./gradlew --no-daemon testDebugUnitTest
./gradlew --no-daemon lintDebug
```
