package com.adminturnos.Listeners;

import com.adminturnos.ObjectInterfaces.ServiceProvider;

public interface ListenerUserManagement {

    public void onComplete(ServiceProvider e);

    void onFailure(String message);
}
