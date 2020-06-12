package com.adminturnos.UserManagment;

/**
 *
 */
public class UserManagment {

    private static UserManagment instance;
    private String accessToken;

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