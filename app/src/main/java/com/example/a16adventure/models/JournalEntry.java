package com.example.a16adventure.models;

public class JournalEntry {
    private String id;
    private String monumentName;
    private String note;
    private String imageUrl;
    private long timestamp;
    private String locationName;

    public JournalEntry() {
        // Required for Firebase
    }

    public JournalEntry(String id, String monumentName, String note, String imageUrl, long timestamp, String locationName) {
        this.id = id;
        this.monumentName = monumentName;
        this.note = note;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.locationName = locationName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMonumentName() { return monumentName; }
    public void setMonumentName(String monumentName) { this.monumentName = monumentName; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
}
