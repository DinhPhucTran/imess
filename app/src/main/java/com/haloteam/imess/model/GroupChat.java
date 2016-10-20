package com.haloteam.imess.model;

import java.util.List;

/**
 * Created by nhonnguyen on 10/20/16.
 */

public class GroupChat {
    private String id;
    private String nameGroup;
    private User sender;
    private List<User> receivers;
    private List<Message> messages;

    public GroupChat(){
    }

    public GroupChat(String id, String nameGroup, User sender, List<User> receivers, List<Message> messages) {
        this.id = id;
        this.nameGroup = nameGroup;
        this.sender = sender;
        this.receivers = receivers;
        this.messages = messages;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public List<User> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
