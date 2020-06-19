package com.adminturnos.ObjectInterfaces;

import java.util.Calendar;
import java.util.List;

public interface Appointment {

    String getId();

    CustomUser getClient();

    List<ServiceInstance> getServiceInstances();

    void addService(ServiceInstance serviceInstance);

    Calendar getDate();

    Calendar getStartTime();

    Calendar getEndTime();
}
