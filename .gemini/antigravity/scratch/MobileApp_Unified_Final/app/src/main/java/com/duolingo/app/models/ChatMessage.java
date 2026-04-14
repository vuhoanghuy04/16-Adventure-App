package com.duolingo.app.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private String text;
    private boolean isUser;
    private String timeFormatted;

    public ChatMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        this.timeFormatted = sdf.format(new Date());
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getTimeFormatted() {
        return timeFormatted;
    }
}
