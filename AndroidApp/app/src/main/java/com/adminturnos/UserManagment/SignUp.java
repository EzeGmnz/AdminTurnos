package com.adminturnos.UserManagment;

import android.content.Intent;

import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.adminturnos.Listeners.ListenerUserManagement;

/**
 * User Registration abstraction
 */
public interface SignUp {

    /**
     * Signs up a user
     */
    void signUp(ListenerUserManagement listener) throws ExceptionEmailInUse;

    /**
     * Handle activity on result
     *
     * @param requestCode request code
     * @param data        intent data
     */
    void onActivityResult(int requestCode, Intent data);
}