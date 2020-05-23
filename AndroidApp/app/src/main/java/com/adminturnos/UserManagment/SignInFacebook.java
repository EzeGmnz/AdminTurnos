package com.adminturnos.UserManagment;

import android.content.Intent;

import com.adminturnos.Exceptions.ExceptionUserAlreadySignedIn;
import com.adminturnos.ObjectInterfaces.ServiceProvider;

/**
 *
 */
public class SignInFacebook implements SignIn {

    /**
     * Default constructor
     */
    public SignInFacebook() {
    }

    @Override
    public void signIn() throws ExceptionUserAlreadySignedIn {

    }

    @Override
    public ServiceProvider onResult(int requestCode, Intent data) {
        return null;
    }
}