package com.example.a16adventure.models;

import com.example.a16adventure.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * LandmarkGalleryDataManager – dữ liệu gallery ảnh + video cho từng di tích.
 *
 * Chỉ giữ ảnh thật (Firebase Storage hoặc drawable local).
 * Đã xoá toàn bộ ảnh placeholder Unsplash / Wikipedia.
 *
 * ===== VIDEO =====
 *   - m1  (Bạch Đằng Giang) → res/raw/video_bachdanggiang.mp4
 *   - m15 (Nhà Hát Lớn)     → res/raw/video_nhahatlon.mp4
 */
public class LandmarkGalleryDataManager {

    private static final Map<String, LandmarkGallery> galleryMap = new HashMap<>();

    private static final String BASE         = "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/";
    private static final String TOKEN_SUFFIX = "?alt=media";

    private static String fb(String filename, String token) {
        return BASE + filename + TOKEN_SUFFIX + "&token=" + token;
    }

    static {
        // ── m1 · Bạch Đằng Giang ─────────────────────────────────────────
        galleryMap.put("m1", new LandmarkGallery(
                "m1",
                "Bạch Đằng Giang",
                "Khu di tích lịch sử quy mô hoành tráng, tái hiện lại 3 trận thủy chiến hào hùng trên dòng sông Bạch Đằng lịch sử. Đây là nơi ghi dấu những chiến thắng oanh liệt của dân tộc Việt Nam trước giặc ngoại xâm.",
                "Thủy Nguyên",
                Arrays.asList(
                        fb("bachdanggiang.webp", "481f2d52-4bae-4579-b1ba-961d7eb262f0"),
                        "bachdanggiang_1",
                        "bachdanggiang_2"
                ),
                R.raw.video_bachdanggiang,
                "Khám phá di tích Bạch Đằng Giang"
        ));

        // ── m3 · Bãi Cọc Cao Quỳ ─────────────────────────────────────────
        galleryMap.put("m3", new LandmarkGallery(
                "m3",
                "Bãi Cọc Cao Quỳ",
                "Bãi cọc gỗ lim vừa được khai quật, là minh chứng lịch sử chấn động cho trận thủy chiến trên sông Bạch Đằng năm 1288. Các cọc gỗ này được đóng xuống đất để ngăn chặn thuyền giặc Nguyên Mông.",
                "Thủy Nguyên",
                Collections.singletonList(
                        fb("baicoccaoquy.webp", "e4326ff4-e0fc-453a-a0a2-3d25a6aba636")
                )
        ));

        // ── m5 · Bảo Tàng Hải Phòng ──────────────────────────────────────
        galleryMap.put("m5", new LandmarkGallery(
                "m5",
                "Bảo Tàng Hải Phòng",
                "Công trình kiến trúc Gothique rêu phong, nơi lưu giữ hàng vạn hiện vật về lịch sử và văn hóa đặc trưng của thành phố Cảng. Bảo tàng là điểm đến không thể bỏ qua khi ghé thăm Hải Phòng.",
                "Hồng Bàng",
                Collections.singletonList(
                        fb("baotanghaiphong.webp", "e36061ac-9838-451a-9177-bf9bfb56fce3")
                )
        ));

        // ── m6 · Bảo Tàng Hải Quân ───────────────────────────────────────
        galleryMap.put("m6", new LandmarkGallery(
                "m6",
                "Bảo Tàng Hải Quân",
                "Nơi lưu giữ những kỷ vật hào hùng của lực lượng hải quân nhân dân Việt Nam và con đường huyền thoại Hồ Chí Minh trên biển. Một điểm đến giáo dục lịch sử quan trọng.",
                "Dương Kinh",
                Collections.singletonList(
                        fb("baotanghaiquan.webp", "ee5b3025-df09-40c8-bf62-224232dee73d")
                )
        ));

        // ── m7 · Bến Nghiêng ─────────────────────────────────────────────
        galleryMap.put("m7", new LandmarkGallery(
                "m7",
                "Bến Nghiêng",
                "Di tích lịch sử hào hùng, nơi chứng kiến những tên lính thực dân Pháp cuối cùng rút khỏi miền Bắc Việt Nam vào năm 1955. Không khí lịch sử trầm mặc vẫn còn vang vọng nơi đây.",
                "Đồ Sơn",
                Collections.singletonList(
                        fb("bennghieng.webp", "4329ad30-4e09-4f37-b53f-58a94b4f2cbe")
                )
        ));

        // ── m8 · Bến Tàu Không Số K15 ────────────────────────────────────
        galleryMap.put("m8", new LandmarkGallery(
                "m8",
                "Bến Tàu Không Số K15",
                "Di tích kiêu hùng, điểm xuất phát của những chuyến tàu không số huyền thoại trên tuyến đường Hồ Chí Minh trên biển, bí mật vận chuyển vũ khí và hàng hóa vào miền Nam trong thời chiến.",
                "Đồ Sơn",
                Collections.singletonList(
                        fb("bentaukhongso.webp", "79fc4891-f889-4384-94b7-fd6849775635")
                )
        ));

        // ── m14 · Ga Hải Phòng ───────────────────────────────────────────
        galleryMap.put("m14", new LandmarkGallery(
                "m14",
                "Ga Hải Phòng",
                "Nhà ga xe lửa cổ kính với kiến trúc tuyệt đẹp, điểm đầu của tuyến đường sắt Hải Phòng - Hà Nội lịch sử. Được xây dựng từ thời Pháp thuộc, ga mang vẻ đẹp hoài cổ độc đáo.",
                "Ngô Quyền",
                Collections.singletonList(
                        fb("gahaiphong.webp", "9958dadf-0331-488b-a8c9-9e9e719aa1a3")
                )
        ));

        // ── m15 · Nhà Hát Lớn Hải Phòng ─────────────────────────────────
        galleryMap.put("m15", new LandmarkGallery(
                "m15",
                "Nhà Hát Lớn Hải Phòng",
                "Một trong 3 nhà hát lớn nhất Việt Nam được Pháp xây dựng, mang đậm dấu ấn kiến trúc Baroque sang trọng. Đây là biểu tượng văn hóa nghệ thuật lâu đời và tự hào của người dân Hải Phòng.",
                "Hồng Bàng",
                Collections.singletonList(
                        fb("nhahatlon.webp", "86953d44-f103-4fe7-a6a8-709e8eb0cfe3")
                ),
                R.raw.video_nhahatlon,
                "Nhà hát lớn Hải Phòng - Kiến trúc Pháp"
        ));

        // ── u3 · Tháp Tường Long ─────────────────────────────────────────
        galleryMap.put("u3", new LandmarkGallery(
                "u3",
                "Tháp Tường Long",
                "Bảo tháp Phật giáo linh thiêng được xây dựng từ triều Lý, tọa lạc uy nghi trên đỉnh núi Ngọc. Tháp Tường Long là minh chứng về sự phát triển rực rỡ của Phật giáo trong thời kỳ nhà Lý.",
                "Đồ Sơn",
                Collections.singletonList(
                        fb("thaptuonglong.webp", "f9d36fe3-1128-492d-b095-2a49dfb42772")
                )
        ));
    }

    public static LandmarkGallery getGallery(String mediaItemId) {
        return galleryMap.get(mediaItemId);
    }

    public static boolean hasGallery(String mediaItemId) {
        return galleryMap.containsKey(mediaItemId);
    }
}
