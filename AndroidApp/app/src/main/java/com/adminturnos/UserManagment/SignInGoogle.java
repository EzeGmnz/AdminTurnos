package com.adminturnos.UserManagment;

import android.content.Intent;

import com.adminturnos.Exceptions.ExceptionUserAlreadySignedIn;
import com.adminturnos.ObjectInterfaces.ServiceProvider;

/**
 *
 */
public class SignInGoogle implements SignIn {

    /**
     * Default constructor
     */
    public SignInGoogle() {
    }


    @Override
    public void signIn() throws ExceptionUserAlreadySignedIn {

    }

    @Override
    public ServiceProvider onResult(int requestCode, Intent data) {
        return null;
    }
}