package com.adminturnos.Database;

import com.squareup.okhttp.Callback;

import java.util.Map;

/**
 *
 */
public interface DatabaseRead {
    void GET(String subDirURL, Map<String, String> params, Callback callback);
}