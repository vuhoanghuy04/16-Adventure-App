package com.example.a16adventure.models;

public class Article {
    private String title;       // Tiêu đề bài viết
    private String category;    // Thể loại (Lịch sử, Ẩm thực...)
    private String timeAndViews; // Thời gian đọc & Lượt xem
    private int imageResource;   // Ảnh minh họa

    // Hàm khởi tạo (Constructor)
    public Article(String title, String category, String timeAndViews, int imageResource) {
        this.title = title;
        this.category = category;
        this.timeAndViews = timeAndViews;
        this.imageResource = imageResource;
    }

    // Các hàm Getter để lấy dữ liệu ra
    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeAndViews() {
        return timeAndViews;
    }

    public int getImageResource() {
        return imageResource;
    }
}