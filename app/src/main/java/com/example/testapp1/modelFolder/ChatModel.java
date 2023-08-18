package com.example.testapp1.modelFolder;

public class ChatModel {
   String messengerid = null;
   String friendid = null;
   String message = null;
   String messagetimenode = null;

    public ChatModel(String friendid,String messengerid, String message, String messagetimenode) {
        this.friendid = friendid;
        this.messengerid = messengerid;
        this.message = message;
        this.messagetimenode = messagetimenode;
    }

    public ChatModel() {
    }

    public String getmessengerid() {
        return messengerid;
    }

    public String getMessage() {
        return message;
    }

    public String getMessagetimenode() {
        return messagetimenode;
    }

    public String getFriendid() {
        return friendid;
    }
}
