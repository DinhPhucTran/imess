package com.haloteam.imess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhonnguyen on 10/20/16.
 */

public class Message {
    private String id;
    private String senderName;
    private String senderId;
    private String senderPhotoUrl;
    private String message;
    private long timeStamp;
    private User owner;

    public Message(){}

    public Message(String senderName, String senderPhotoUrl, String message, long timeStamp) {
        this.senderName = senderName;
        this.senderPhotoUrl = senderPhotoUrl;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhotoUrl() {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl) {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
