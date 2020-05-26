package com.adminturnos.Database;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

/**
 *
 */
public class ReadPostgreSQL implements DatabaseRead {

    private static ReadPostgreSQL instance;
    private static OkHttpClient client;

    /**
     * Default constructor
     */
    private ReadPostgreSQL() {
        client = new OkHttpClient();
        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {

                response.request().newBuilder().header("Authorization",
                        // Getting access token
                        AccessToken.getInstance().getAccessToken()).build();
                return null;
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                return null;
            }
        });
    }

    public static ReadPostgreSQL getInstance() {
        if (instance == null) {
            instance = new ReadPostgreSQL();
        }
        return instance;
    }

    public void GET(String URL, RequestBody requestBody, Callback callback) {

        final Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

}