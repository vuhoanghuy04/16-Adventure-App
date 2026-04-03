package com.example.a16adventure.models;

public class Message {
    private String role; // "user" hoặc "model"
    private String content;
    private String imageUrl;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public Message(String role, String content, String imageUrl) {
        this.role = role;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}