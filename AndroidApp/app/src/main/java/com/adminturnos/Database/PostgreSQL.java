package com.adminturnos.Database;

import com.adminturnos.Listeners.ListenerDatabase;
import com.adminturnos.Values;

/**
 *
 */
public class PostgreSQL implements Database {

    private static PostgreSQL instance;

    private String URL = "jdbc:postgresql://192.168.0.4:5432/" + Values.POSTGRESQL_DATABASE_NAME;

    /**
     * Default constructor
     */
    private PostgreSQL() {

    }

    public static PostgreSQL getInstance() {
        if (instance == null) {
            instance = new PostgreSQL();
        }
        return instance;
    }

    @Override
    public void execute(ListenerDatabase listener, String a) {

        String json = "";
        listener.onSuccess(json);

    }
}