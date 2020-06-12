package com.adminturnos.Database;

import com.adminturnos.UserManagment.UserManagment;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.net.Proxy;
import java.util.Map;

/**
 *
 */
public class DatabaseDjangoWrite implements DatabaseWrite {

    private static DatabaseDjangoWrite instance;
    private static OkHttpClient client;
    private static Database database;

    /**
     * Default constructor
     */
    private DatabaseDjangoWrite() {
        this.database = new DatabaseDjango();
        client = new OkHttpClient();
        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) {

                response.request().newBuilder().header("Authorization",
                        UserManagment.getInstance().getAccessToken()).build();
                return null;
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) {
                return null;
            }
        });
    }

    public static DatabaseDjangoWrite getInstance() {
        if (instance == null) {
            instance = new DatabaseDjangoWrite();
        }
        return instance;
    }

    public void POST(String subDirURL, Map<String, String> body, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(database.getUrl() + subDirURL).newBuilder();
        FormEncodingBuilder requestBodyBuilder = new FormEncodingBuilder();

        if (body != null) {
            for (Map.Entry<String, String> entry : body.entrySet()) {
                requestBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBodyBuilder.build())
                .build();
        client.newCall(request).enqueue(callback);
    }

}