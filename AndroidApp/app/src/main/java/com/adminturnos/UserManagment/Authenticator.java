package com.adminturnos.UserManagment;

import android.content.Intent;

import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.google.android.gms.tasks.OnCompleteListener;

/**
 * User Registration abstraction
 */
public interface Authenticator {

    Intent getSignUpIntent() throws ExceptionEmailInUse;

    /**
     * Handle activity on result
     *
     * @param data intent data
     */
    void onActivityResult(Intent data);

    void exchangeTokenID(String idToken);

    void signOut(OnCompleteListener<Void> listener);
}