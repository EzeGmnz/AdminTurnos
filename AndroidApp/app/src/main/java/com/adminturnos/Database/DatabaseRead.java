package com.adminturnos.Database;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.RequestBody;

/**
 *
 */
public interface DatabaseRead {
    void GET(String subDirURL, RequestBody requestBody, Callback callback);
}