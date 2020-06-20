package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public interface Appointment extends Serializable {

    String getId();

    CustomUser getClient();

    List<ServiceInstance> getServiceInstances();

    void setServiceInstances(List<ServiceInstance> list);

    Calendar getDate();
}
