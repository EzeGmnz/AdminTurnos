package com.adminturnos.Database;

import com.adminturnos.Listeners.DatabaseCallback;

import java.util.Map;

/**
 *
 */
public interface DatabaseWrite {

    void POST(String subDirURL, Map<String, String> body, DatabaseCallback callback);

}