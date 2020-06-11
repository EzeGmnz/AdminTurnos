package com.adminturnos.UserManagment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Listeners.ListenerAuthenticator;
import com.adminturnos.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
            listener.onComplete(Activity.RESULT_CANCELED);
        }

    }

    @Override
    public void exchangeTokenID(String idToken) {
        RequestBody requestBody = new FormEncodingBuilder()
                .add("id_token", idToken)
                .build();

        DatabaseDjangoRead.getInstance().GET(Values.DJANGO_URL_CONVERT_TOKEN, requestBody, new CallbackExchangeTokenId());
    }

    private class CallbackExchangeTokenId implements Callback {
        @Override
        public void onResponse(Response response) throws IOException {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                UserManagment.getInstance().setAccessToken(jsonObject.getString("access_token"));
                listener.onComplete(Activity.RESULT_OK);
            } catch (JSONException e) {
                listener.onComplete(Activity.RESULT_CANCELED);
            }
        }

        @Override
        public void onFailure(final Request request, final IOException e) {
            Log.e("FAILED", e.toString());
        }
    }

}