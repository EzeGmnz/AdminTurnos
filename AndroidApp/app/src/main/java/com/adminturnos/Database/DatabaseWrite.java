package com.adminturnos.Database;

import com.adminturnos.Listeners.DatabaseCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 *
 */
public interface DatabaseWrite {

    void POST(String subDirURL, Map<String, String> body, DatabaseCallback callback);

    void POSTJSON(String subDirURL, JSONObject json, DatabaseCallback callback);

    void invalidate();
}