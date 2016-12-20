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
    private String messageContent;
    private long timeStamp;

    public Message(){}

    public Message(String senderName, String senderPhotoUrl, String messageContent, long timeStamp) {
        this.senderName = senderName;
        this.senderPhotoUrl = senderPhotoUrl;
        this.messageContent = messageContent;
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

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
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
}
