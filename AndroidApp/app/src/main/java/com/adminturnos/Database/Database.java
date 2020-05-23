package com.adminturnos.Database;

import com.adminturnos.Listeners.ListenerDatabase;

/**
 *
 */
public interface Database {

    public abstract void execute(ListenerDatabase listener, String a);

}