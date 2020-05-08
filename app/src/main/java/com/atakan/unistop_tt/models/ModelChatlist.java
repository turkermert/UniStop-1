package com.atakan.unistop_tt.models;

public class ModelChatlist {
    String id; //to get chatlist, sender/receiver uid


    public ModelChatlist(String id) {
        this.id = id;
    }

    public ModelChatlist() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
