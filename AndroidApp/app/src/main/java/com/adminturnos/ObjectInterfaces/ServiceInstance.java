package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;
import java.util.Calendar;

public interface ServiceInstance extends Serializable {

    String getId();

    Calendar getDateTime();

    Service getService();

}
