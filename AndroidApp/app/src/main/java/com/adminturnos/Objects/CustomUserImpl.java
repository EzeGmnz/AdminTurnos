package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.CustomUser;

import java.util.Objects;

/**
 *
 */
public class CustomUserImpl implements CustomUser {

    private String givenName, familyName, email, id;

    public CustomUserImpl(String id, String givenName, String familyName, String email) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserImpl that = (CustomUserImpl) o;
        return Objects.equals(givenName, that.givenName) &&
                Objects.equals(familyName, that.familyName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(id, that.id);
    }

}