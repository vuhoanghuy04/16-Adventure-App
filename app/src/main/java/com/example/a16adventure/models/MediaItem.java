package com.example.a16adventure.models;

import java.io.Serializable;

public class MediaItem implements Serializable {
    private String id;
    private String title;
    private String category; // Lễ hội, Di tích, Đặc sản...
    private String type; // "image" hoặc "video"
    private String url;
    private String thumbnailUrl; // Dành cho video (hiện ảnh trước), nếu ảnh thì trùng url

    public MediaItem() {
    }

    public MediaItem(String id, String title, String category, String type, String url) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.type = type;
        this.url = url;
        this.thumbnailUrl = url;
    }

    public MediaItem(String id, String title, String category, String type, String url, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.type = type;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getUrl() { return url; }
    public String getThumbnailUrl() { return thumbnailUrl; }
}
