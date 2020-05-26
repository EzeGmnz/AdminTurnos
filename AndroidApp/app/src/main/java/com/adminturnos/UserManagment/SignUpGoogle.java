package com.adminturnos.UserManagment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.adminturnos.Builder.BuilderServiceProvider;
import com.adminturnos.Database.AccessToken;
import com.adminturnos.Database.ReadPostgreSQL;
import com.adminturnos.Listeners.ListenerDatabase;
import com.adminturnos.Listeners.ListenerUserManagement;
import com.adminturnos.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                .requestIdToken(Values.CLIENT_ID_WEB_APP)
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
                final String idToken = account.getIdToken();

                RequestBody requestBody = new FormEncodingBuilder()
                        .add("id_token", idToken)
                        .build();

                ReadPostgreSQL.getInstance().GET(Values.DJANGO_URL_CONVERT_TOKEN, requestBody, new Callback() {
                    @Override
                    public void onFailure(final Request request, final IOException e) {
                        Log.e("AAAAA", e.toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            final String accessToken = jsonObject.getString("access_token");
                            AccessToken.getInstance().setAccessToken(accessToken);
                            listener.onComplete(null);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void createServiceProvider(String displayName, String email) {

        /**
         * Creating ServiceProvider
         */
        /*PostgreSQL.getInstance().execute(
                new ListenerDatabaseImp(),
                String.format("select * from \"newServiceProvider\"(%1$s, %2$s)", displayName, email));*/

        // ...k

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