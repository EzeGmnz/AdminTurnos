package com.adminturnos.Database;

import com.adminturnos.Listeners.DatabaseCallback;
import com.adminturnos.UserManagment.UserManagment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 *
 */
public class DatabaseDjangoWrite implements DatabaseWrite {

    private static DatabaseDjangoWrite instance;
    private static AsyncHttpClient client;
    private static Database database;

    /**
     * Default constructor
     */
    private DatabaseDjangoWrite() {
        database = new DatabaseDjango();
        client = new AsyncHttpClient();
        client.addHeader("Authorization", "Token " + UserManagment.getInstance().getAccessToken());
    }

    public static DatabaseDjangoWrite getInstance() {
        if (instance == null) {
            instance = new DatabaseDjangoWrite();
        }
        return instance;
    }

    @Override
    public void POST(String subDirURL, Map<String, String> body, DatabaseCallback callback) {

        RequestParams params = new RequestParams();
        if (body != null) {
            for (Map.Entry<String, String> entry : body.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        String url = database.getUrl() + subDirURL;

        client.post(
                url,
                params,
                callback
        );
    }

    public void POSTJSON(String subDirURL, JSONObject json, DatabaseCallback callback) {
        StringEntity entity = null;

        try {
            entity = new StringEntity(json.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = database.getUrl() + subDirURL;

        client.post(
                null,
                url,
                entity,
                "application/json",
                callback
        );
    }

}