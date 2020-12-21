package com.example.firebaseproject.util;

import android.app.Application;

public class JournalApi extends Application {

    public  static  JournalApi instance ;
    private String username;
    private  String userId;

    // creating a singleton .. i.e create on unique object for entire app


    public static JournalApi getInstance() {
        if(instance == null)
        {
            instance  = new JournalApi();

        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JournalApi() {
    }

    public JournalApi(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }
}
