package com.example.a16adventure.models;

public class Article {
    private String title;
    private String category;
    private String timeAndViews;
    private String content;   // Nội dung chi tiết bài viết
    private String imageUrl;  // Link ảnh từ Firebase
    private boolean isFeatured; // Cờ đánh dấu mục Nổi bật

    // 1. HÀM TẠO TRỐNG BẮT BUỘC PHẢI CÓ ĐỂ FIREBASE ĐỌC DỮ LIỆU
    public Article() {
    }

    // 2. Hàm tạo có tham số (tùy chọn sử dụng)
    public Article(String title, String category, String timeAndViews, String content, String imageUrl, boolean isFeatured) {
        this.title = title;
        this.category = category;
        this.timeAndViews = timeAndViews;
        this.content = content;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
    }

    // --- CÁC HÀM GETTER VÀ SETTER ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTimeAndViews() { return timeAndViews; }
    public void setTimeAndViews(String timeAndViews) { this.timeAndViews = timeAndViews; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(boolean isFeatured) { this.isFeatured = isFeatured; }
}