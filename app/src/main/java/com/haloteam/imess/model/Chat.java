package com.haloteam.imess.model;

import java.util.List;

/**
 * Created by DinhPhuc on 15/10/2016.
 */

public class Chat {
    private String id;
    private String name;
    private List<String> memberIds;

    public Chat() {
    }

    public Chat(String name, List<String> memberIds) {
        this.name = name;
        this.memberIds = memberIds;
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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}
