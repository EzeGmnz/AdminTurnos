package com.adminturnos.UserManagment;

import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.adminturnos.ObjectInterfaces.ServiceProvider;

/**
 * User Registration abstraction
 */
public interface SignUp {

    /**
     * Signs up a user
     *
     * @return ServiceProvider signup
     */
    ServiceProvider signUp() throws ExceptionEmailInUse;

}