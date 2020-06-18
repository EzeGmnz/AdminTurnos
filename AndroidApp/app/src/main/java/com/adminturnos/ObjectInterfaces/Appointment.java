package com.adminturnos.ObjectInterfaces;

import java.util.Calendar;
import java.util.List;

public interface Appointment {

    String getId();

    List<ServiceInstance> getServices();

    void addService(ServiceInstance serviceInstance);

    Calendar getDate();
}
