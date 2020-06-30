package com.adminturnos.Listeners;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public abstract class DatabaseCallback extends JsonHttpResponseHandler {

    public abstract void onSuccess(int statusCode, Header[] headers, JSONObject response);

}
