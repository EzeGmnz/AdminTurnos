package com.adminturnos.UserManagment;

import com.adminturnos.ObjectInterfaces.ServiceProvider;

/**
 * SignIn Abstraction
 */
public interface SignIn {

    /**
     * Signs in and returns ServiceProvider
     */
    ServiceProvider signIn();

}