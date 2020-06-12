package com.adminturnos.Database;

import com.adminturnos.UserManagment.UserManagment;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.net.Proxy;
import java.util.Map;

/**
 *
 */
public class DatabaseDjangoRead implements DatabaseRead {

    private static DatabaseDjangoRead instance;
    private static OkHttpClient client;
    private static Database database;

    /**
     * Default constructor
     */
    private DatabaseDjangoRead() {
        this.database = new DatabaseDjango();
        client = new OkHttpClient();
        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) {

                return response.request().newBuilder().header("Authorization",
                        "Token " + UserManagment.getInstance().getAccessToken()).build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) {
                return null;
            }
        });
    }

    public static DatabaseDjangoRead getInstance() {
        if (instance == null) {
            instance = new DatabaseDjangoRead();
        }
        return instance;
    }

    public void GET(String subDirURL, Map<String, String> params, Callback callback) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(database.getUrl() + subDirURL).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

}