package com.example.a16adventure.models; // Thay bằng tên package thực tế của dự án

public class Monument {
    private String id;
    private String name;        // Tên di tích (VD: Đền Nghè)
    private String district;    // Quận/Huyện (VD: Lê Chân)
    private String description; // Mô tả ngắn
    private String imageUrl;    // Link ảnh để Glide tải về
    private double latitude;    // Vĩ độ cho Google Maps
    private double longitude;   // Kinh độ cho Google Maps

    // Constructor rỗng (cần thiết nếu sau này dùng Firebase)
    public Monument() {
    }

    // Constructor đầy đủ
    public Monument(String id, String name, String district, String description, String imageUrl, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.description = description;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Click chuột phải > Generate > Getter and Setter để tự động tạo các hàm get/set cho toàn bộ biến
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}