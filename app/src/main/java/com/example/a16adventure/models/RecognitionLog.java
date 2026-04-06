package com.example.a16adventure.models;

import com.google.firebase.Timestamp;

public class RecognitionLog {
    private String logId;
    private String userId;
    private String imageUrl;
    private String placeName;
    private String description;
    private float confidence;
    private Timestamp timestamp;

    public RecognitionLog() {} // Cần thiết cho Firebase

    public RecognitionLog(String userId, String imageUrl, String placeName, String description, float confidence) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.placeName = placeName;
        this.description = description;
        this.confidence = confidence;
        this.timestamp = Timestamp.now();
    }

    // Getters và Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; }
    public String getPlaceName() { return placeName; }
    public String getDescription() { return description; }
    public float getConfidence() { return confidence; }
    public Timestamp getTimestamp() { return timestamp; }
}