package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.CustomUser;

/**
 *
 */
public class ServiceProvider implements CustomUser {

    private String givenName, familyName, email, id;

    public ServiceProvider(String id, String givenName, String familyName, String email) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
        this.id = id;
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public String getName() {
        return givenName + " " + familyName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }
}