package com.example.a16adventure.domain.model;

public class UserSession {
    private final String uid;
    private final String displayName;
    private final String email;

    public UserSession(String uid, String displayName, String email) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}
