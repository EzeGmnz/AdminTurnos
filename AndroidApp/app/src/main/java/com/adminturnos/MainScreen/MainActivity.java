package com.adminturnos.MainScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adminturnos.Listeners.ListenerAuthenticator;
import com.adminturnos.R;
import com.adminturnos.UserManagment.ActivitySignIn;
import com.adminturnos.UserManagment.Authenticator;
import com.adminturnos.UserManagment.AuthenticatorGoogle;
import com.adminturnos.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends AppCompatActivity {

    Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUserIsAuthenticated();
    }

    private void checkUserIsAuthenticated() {
        GoogleSignInAccount account = getAuthenticatedUser();
        if (account != null) {
            refreshAccessToken(account);
        } else {
            displayUILogIn();
        }
    }

    private GoogleSignInAccount getAuthenticatedUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        return account;
    }

    private void displayUILogIn() {
        Intent intent = new Intent(this, ActivitySignIn.class);
        startActivityForResult(intent, Values.RC_SIGN_IN_ACTIVITY);
    }

    public void refreshAccessToken(GoogleSignInAccount account) {
        authenticator = new AuthenticatorGoogle(this, new ListenerUserManagementRefresh());
        authenticator.exchangeTokenID(account.getIdToken());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Values.RC_SIGN_IN_ACTIVITY) {
            checkUserIsAuthenticated();
        }
    }

    private void displayUIAuthenticated() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_main_screen, new FragmentMain());
        transaction.commit();
    }

    private class ListenerUserManagementRefresh implements ListenerAuthenticator {

        @Override
        public void onComplete(int resultCode) {
            displayUIAuthenticated();
        }

    }
}
