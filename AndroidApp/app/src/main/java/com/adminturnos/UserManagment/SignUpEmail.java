package com.adminturnos.UserManagment;

import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.adminturnos.ObjectInterfaces.ServiceProvider;

/**
 *
 */
public class SignUpEmail implements SignUp {

    /**
     * Default constructor
     */
    public SignUpEmail() {
    }

    @Override
    public ServiceProvider signUp() throws ExceptionEmailInUse {
        return null;
    }
}