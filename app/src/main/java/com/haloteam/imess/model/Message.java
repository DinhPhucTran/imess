package com.haloteam.imess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhonnguyen on 10/20/16.
 */

public class Message {
    private String id;
    private User writer;
    private List<User> reader;
    private String messageContent;

    public Message(){
    }

//    public Message(String id, User writer, List<User> reader) {
//        this.id = id;
//        this.writer = writer;
//        this.reader = reader;
//    }

    public Message(String id, User writer, List<User> reader, String messageContent) {
        this.id = id;
        this.writer = writer;
        this.reader = reader;
        this.messageContent = messageContent;
    }

    public static List<Message> createListMessage(int numberMessage){
        List<Message> mess = new ArrayList<>();
        List<User> userInGroup = new ArrayList<User>();
        for (int i = 0; i < 2; i++){
            if (i % 2 == 0){
                userInGroup.add(new User("a"));
            } else {
                userInGroup.add(new User("b"));
            }
        }
        for (int i = 0; i < numberMessage; i++) {
            if (i % 3 == 0) {
                mess.add(new Message("Message " + i, new User("a"), userInGroup, "djfldjfklsjfklsdjfljsdfkjsdflkjfljslfkjsdifeijojfsdjfijflsjf"));
            } else {
                mess.add(new Message("Message " + i, new User("b"), userInGroup, "djfldjfklsjfklsdjfljsdfkjsdflkjfljslfkjsdifeijojfsdjfijflsjf"));
            }
        }
        return mess;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public List<User> getReader() {
        return reader;
    }

    public void setReader(List<User> reader) {
        this.reader = reader;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
