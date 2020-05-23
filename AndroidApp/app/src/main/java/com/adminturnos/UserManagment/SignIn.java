package com.adminturnos.UserManagment;

import android.content.Intent;

import com.adminturnos.Exceptions.ExceptionUserAlreadySignedIn;
import com.adminturnos.ObjectInterfaces.ServiceProvider;

/**
 * SignIn Abstraction
 */
public interface SignIn {

    /**
     * Signs in and returns ServiceProvider
     */
    void signIn() throws ExceptionUserAlreadySignedIn;

    /**
     * Handle activity on result
     *
     * @param requestCode request code
     * @param data        intent data
     * @return built serviceProvider
     */
    ServiceProvider onResult(int requestCode, Intent data);
}