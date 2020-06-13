package com.adminturnos.UserManagment;

import com.google.android.gms.tasks.OnCompleteListener;

/**
 *
 */
public class UserManagment {

    private static UserManagment instance;
    private String accessToken;
    private Authenticator authenticator;

    private UserManagment() {
        this.accessToken = null;
        this.authenticator = null;
    }

    public static UserManagment getInstance() {
        if (instance == null) {
            instance = new UserManagment();
        }
        return instance;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void signOut(OnCompleteListener<Void> listener) {
        this.authenticator.signOut(listener);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void invalidate() {
        instance = new UserManagment();
    }

}