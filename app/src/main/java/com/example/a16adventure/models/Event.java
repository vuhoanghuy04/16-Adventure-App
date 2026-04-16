package com.example.a16adventure.models;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {
    private String id;
    private String name;
    private String date; // Tháng mấy, hoặc ngày định dạng dd/MM/yyyy
    private String location;
    private String shortDescription;
    private String originMeaning;
    private List<String> mainActivities;
    private String imageUrl;
    
    // Thuộc tính phụ cho bộ lọc
    private int monthLimit; // Định dạng số để dễ lọc theo tháng (1-12)

    public Event() {
    }

    public Event(String id, String name, String date, String location, String shortDescription, String originMeaning, List<String> mainActivities, String imageUrl, int monthLimit) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.shortDescription = shortDescription;
        this.originMeaning = originMeaning;
        this.mainActivities = mainActivities;
        this.imageUrl = imageUrl;
        this.monthLimit = monthLimit;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getOriginMeaning() { return originMeaning; }
    public void setOriginMeaning(String originMeaning) { this.originMeaning = originMeaning; }

    public List<String> getMainActivities() { return mainActivities; }
    public void setMainActivities(List<String> mainActivities) { this.mainActivities = mainActivities; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getMonthLimit() { return monthLimit; }
    public void setMonthLimit(int monthLimit) { this.monthLimit = monthLimit; }
}
