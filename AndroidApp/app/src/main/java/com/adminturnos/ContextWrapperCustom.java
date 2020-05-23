package com.adminturnos;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Custom context wrapper, must be set
 */
public class ContextWrapperCustom extends ContextWrapper {

    private static ContextWrapperCustom instance;

    private ContextWrapperCustom(Context base) {
        super(base);
    }

    public static ContextWrapperCustom getInstance() {
        if (instance == null) {
            instance = new ContextWrapperCustom(null);
        }
        return instance;
    }

    public Context getContext() {
        return this.getBaseContext();
    }

    public void setContext(Context context) {
        instance = new ContextWrapperCustom(null);
    }
}
