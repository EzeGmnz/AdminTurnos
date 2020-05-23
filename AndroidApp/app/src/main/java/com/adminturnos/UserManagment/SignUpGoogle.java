package com.adminturnos.UserManagment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.adminturnos.Builder.BuilderServiceProvider;
import com.adminturnos.Database.PostgreSQL;
import com.adminturnos.Listeners.ListenerDatabase;
import com.adminturnos.Listeners.ListenerUserManagement;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static com.adminturnos.Values.RC_SIGN_UP;

/**
 *
 */
public class SignUpGoogle implements SignUp {

    /**
     * Base Activity
     */
    private Activity activity;

    /**
     * Google Sign In Options
     */
    private GoogleSignInOptions gso;

    /**
     * Google Sign in Client
     */
    private GoogleSignInClient googleSignInClient;

    /**
     * Listener to call when completed
     */
    private ListenerUserManagement listener;

    public SignUpGoogle(Activity act) {

        this.activity = act;
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    @Override
    public void signUp(ListenerUserManagement listener) {
        this.listener = listener;
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_UP);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {

        if (requestCode == RC_SIGN_UP) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);

                createServiceProvider(account.getDisplayName(), account.getEmail());

            } catch (ApiException e) {
                //TODO
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
            }
        }
    }

    private void createServiceProvider(String displayName, String email) {

        /**
         * Creating ServiceProvider
         */
        PostgreSQL.getInstance().execute(
                new ListenerDatabaseImp(),
                String.format("select * from \"newServiceProvider\"(%1$s, %2$s)", displayName, email));
    }


    public class ListenerDatabaseImp implements ListenerDatabase {

        @Override
        public void onSuccess(String json) {

            // Building Service Provider from database
            Log.e("AAA",
                    new BuilderServiceProvider()
                            .build(json)
                            .toString());
        }

        @Override
        public void onFailure(String error) {
            Log.e("AAA",
                    error);
        }
    }
}