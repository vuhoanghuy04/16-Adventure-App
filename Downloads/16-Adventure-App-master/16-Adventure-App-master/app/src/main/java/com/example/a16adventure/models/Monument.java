package com.example.a16adventure.models;

public class Monument {
    private String id;
    private String name;
    private String district;
    private String description;
    private String imageUrl;
    private double latitude;
    private double longitude;

    // --- 2 BIẾN MỚI ĐỂ LƯU TRẠNG THÁI THÍCH/LƯU ---
    private boolean isLiked = false;
    private boolean isSaved = false;

    public Monument() {
    }

    public Monument(String id, String name, String district, String description, String imageUrl, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.description = description;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

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

    // --- CÁC HÀM GET/SET CHO 2 BIẾN MỚI ---
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
}