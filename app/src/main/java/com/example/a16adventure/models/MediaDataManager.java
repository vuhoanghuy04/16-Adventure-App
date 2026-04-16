package com.example.a16adventure.models;

import java.util.ArrayList;
import java.util.List;

public class MediaDataManager {

    public static List<MediaItem> getMockMedia() {
        List<MediaItem> list = new ArrayList<>();

        // Sử dụng ảnh thực tế trên Firebase cho các Di tích / Phong cảnh
        list.add(new MediaItem("m1", "Bạch Đằng Giang", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/bachdanggiang.webp?alt=media&token=481f2d52-4bae-4579-b1ba-961d7eb262f0"));
        list.add(new MediaItem("m2", "Bạch Long Vĩ", "Phong cảnh", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/bachlongvy.webp?alt=media&token=caa69189-69fd-42ff-b136-5658dfddf63a"));
        list.add(new MediaItem("m3", "Bãi Cọc Cao Quỳ", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/baicoccaoquy.webp?alt=media&token=e4326ff4-e0fc-453a-a0a2-3d25a6aba636"));
        list.add(new MediaItem("m4", "Bãi Tắm Cát Cò", "Phong cảnh", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/baitamcatco.webp?alt=media&token=451fa0e4-ded7-42e2-b0d6-6d525fe94f23"));
        list.add(new MediaItem("m5", "Bảo Tàng Hải Phòng", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/baotanghaiphong.webp?alt=media&token=e36061ac-9838-451a-9177-bf9bfb56fce3"));
        list.add(new MediaItem("m6", "Bảo Tàng Hải Quân", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/baotanghaiquan.webp?alt=media&token=ee5b3025-df09-40c8-bf62-224232dee73d"));
        list.add(new MediaItem("m7", "Bến Nghiêng", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/bennghieng.webp?alt=media&token=4329ad30-4e09-4f37-b53f-58a94b4f2cbe"));
        list.add(new MediaItem("m8", "Bến Tàu Không Số", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/bentaukhongso.webp?alt=media&token=79fc4891-f889-4384-94b7-fd6849775635"));
        list.add(new MediaItem("m9", "Đảo Cát Bà", "Phong cảnh", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/catba.webp?alt=media&token=af608b21-400c-420e-a3c0-045233798fa8"));
        list.add(new MediaItem("m10", "Cầu Bính", "Phong cảnh", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/caubinh.webp?alt=media&token=36898ba1-5a6e-4c5b-a73d-cd268a1676f7"));
        list.add(new MediaItem("m11", "Cầu Hoàng Văn Thụ", "Phong cảnh", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/cauhoangvanthu.webp?alt=media&token=670016d4-9136-462d-a685-6d2949f60be1"));
        list.add(new MediaItem("m14", "Ga Hải Phòng", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/gahaiphong.webp?alt=media&token=9958dadf-0331-488b-a8c9-9e9e719aa1a3"));
        list.add(new MediaItem("m15", "Nhà hát lớn Hải Phòng", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/nhahatlon.webp?alt=media&token=86953d44-f103-4fe7-a6a8-709e8eb0cfe3"));

        list.add(new MediaItem("u1", "Vịnh Lan Hạ", "Phong cảnh", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/vinhlanha.webp?alt=media&token=9579d26d-b633-4434-a42f-e2c2cc122599"));
        list.add(new MediaItem("u3", "Tháp Tường Long", "Di tích", "image", "https://firebasestorage.googleapis.com/v0/b/adventure-23ee9.firebasestorage.app/o/thaptuonglong.webp?alt=media&token=f9d36fe3-1128-492d-b095-2a49dfb42772"));
        
        // Thêm hình ảnh từ file Drawable cục bộ theo ý người dùng
        list.add(new MediaItem("u2", "Đèn Lồng Đêm", "Lễ hội", "image", "media_den_long_dem"));
        list.add(new MediaItem("u4", "Món Phở Đậm Vị", "Ẩm thực", "image", "media_pho_dam_vi"));

        list.add(new MediaItem("m12", "Đặc sản Bánh Mì Cay", "Ẩm thực", "image", "media_banh_mi_cay"));
        list.add(new MediaItem("m13", "Nem Cua Bể Hải Phòng", "Ẩm thực", "image", "media_nem_cua_be"));

        // Video - Sử dụng ảnh đại diện thumbnail trích xuất từ file cục bộ
        list.add(new MediaItem("v1", "Trải nghiệm Lễ hội", "Lễ hội", "video", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4", "media_trai_nghiem_le_hoi"));

        list.add(new MediaItem("u5", "Lễ hội Hoa Đăng", "Lễ hội", "image", "media_le_hoi_hoa_dang"));
        list.add(new MediaItem("u6", "Đêm Nhạc Sôi Động", "Lễ hội", "image", "media_dem_nhac_soi_dong"));
        list.add(new MediaItem("u7", "Sự kiện Văn hóa", "Lễ hội", "image", "media_su_kien_van_hoa"));
        list.add(new MediaItem("u8", "Cuộc biểu diễn lớn", "Lễ hội", "image", "media_cuoc_bieu_dien_lon"));

        list.add(new MediaItem("u9", "Trình Bày Món Ăn", "Ẩm thực", "image", "media_trinh_bay_mon_an"));
        list.add(new MediaItem("u10", "Bánh Mì Truyền Thống", "Ẩm thực", "image", "media_banh_mi_truyen_thong"));
        list.add(new MediaItem("u11", "Bún Phở Sợi Xèo", "Ẩm thực", "image", "media_bun_pho_soi_xeo"));

        return list;
    }
}
