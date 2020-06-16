package com.adminturnos.Database;

import java.util.Map;

/**
 *
 */
public interface DatabaseRead {

    void GET(String subDirURL, Map<String, String> body, DatabaseCallback callback);

}