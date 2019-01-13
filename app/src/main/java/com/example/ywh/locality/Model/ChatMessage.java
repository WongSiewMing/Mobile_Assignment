package com.example.ywh.locality.Model;

import java.util.Date;

public class ChatMessage {
    private String senderID;
    private String senderName;
    private String group;
    private String message;
    private long sentTime;

    public ChatMessage(String senderID, String senderName, String group, String message) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.group = group;
        this.message = message;
        this.sentTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSentTime() {
        return sentTime;
    }
}
