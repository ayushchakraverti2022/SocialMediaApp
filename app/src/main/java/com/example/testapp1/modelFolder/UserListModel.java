package com.example.testapp1.modelFolder;

import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class UserListModel {

    String useridentity = null;
    DataSnapshot snapshot;
    String userimage =null;
    String username = null;


    public UserListModel() {

    }



    public UserListModel(String userimage, String username) {
        this.userimage = userimage;
        char a = Character.toUpperCase(username.charAt(0));
        this.username = a+username.substring(1);

    }


    public UserListModel(String useridentity, String userimage, String username , DataSnapshot snapshot) {
        this.useridentity = useridentity;
        this.snapshot = snapshot;
        this.userimage = userimage;
        char a = Character.toUpperCase(username.charAt(0));
        this.username = a+username.substring(1);

    }


    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseridentity() {
        return useridentity;
    }

    public DataSnapshot getSnapshot() {
        return snapshot;
    }
}
