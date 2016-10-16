package com.haloteam.imess.model;

import java.util.List;

/**
 * Created by DinhPhuc on 15/10/2016.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String oneSignalId;
    private String photoUrl;
    private List<String> friendIdList;

    public User(){}

    public User(String name, String email, String oneSignalId, List<String> friendIdList) {
        this.name = name;
        this.email = email;
        this.oneSignalId = oneSignalId;
        this.friendIdList = friendIdList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOneSignalId() {
        return oneSignalId;
    }

    public void setOneSignalId(String oneSignalId) {
        this.oneSignalId = oneSignalId;
    }

    public List<String> getfriendIdList() {
        return friendIdList;
    }

    public void setfriendIdList(List<String> friendIdList) {
        this.friendIdList = friendIdList;
    }

    public void addFriendId(String id){
        friendIdList.add(id);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
