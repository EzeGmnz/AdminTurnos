package com.adminturnos.UserManagment;

import com.adminturnos.Listeners.ListenerAuthenticator;

/**
 *
 */
public class UserManagment {

    private static UserManagment instance;
    private String accessToken;
    private ListenerAuthenticator listener;

    private UserManagment() {
        accessToken = null;
    }

    public static UserManagment getInstance() {
        if (instance == null) {
            instance = new UserManagment();
        }
        return instance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}