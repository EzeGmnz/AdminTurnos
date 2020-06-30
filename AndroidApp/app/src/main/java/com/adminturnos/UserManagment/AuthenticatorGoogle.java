package com.adminturnos.UserManagment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.Listeners.DatabaseCallback;
import com.adminturnos.Listeners.ListenerAuthenticator;
import com.adminturnos.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 */
public class AuthenticatorGoogle implements Authenticator {

    /**
     * Base Activity
     */
    private Activity activity;

    /**
     * Google Sign in Client
     */
    private GoogleSignInClient googleSignInClient;

    /**
     * Listener to call when completed
     */
    private ListenerAuthenticator listener;

    public AuthenticatorGoogle(Activity act, ListenerAuthenticator listener) {
        this.listener = listener;
        this.activity = act;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Values.CLIENT_ID_WEB_APP)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(act, gso);
    }

    @Override
    public Intent getSignUpIntent() {
        return googleSignInClient.getSignInIntent();
    }

    @Override
    public void onActivityResult(Intent data) {

        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            exchangeTokenID(account.getIdToken());
        } catch (ApiException e) {
            e.printStackTrace();
            listener.onComplete(Activity.RESULT_OK);
        }

    }

    public void signOut(OnCompleteListener<Void> listener) {
        googleSignInClient.signOut()
                .addOnCompleteListener(activity, listener);
    }

    @Override
    public void exchangeTokenID(String idToken) {
        Map<String, String> body = new HashMap<>();
        body.put("id_token", idToken);

        DatabaseDjangoWrite.getInstance().POST(Values.DJANGO_URL_CONVERT_TOKEN, body, new CallbackExchangeTokenId());
    }

    private class CallbackExchangeTokenId extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {

                SharedPreferences sharedPreferences = activity.getSharedPreferences(Values.SHARED_PREF_NAME, MODE_PRIVATE);
                sharedPreferences.edit().putString(Values.SHARED_PREF_ACCESS_TOKEN, response.getString("access_token")).apply();
                listener.onComplete(Activity.RESULT_OK);
            } catch (JSONException e) {
                listener.onComplete(Activity.RESULT_OK);
            }
        }
    }

}