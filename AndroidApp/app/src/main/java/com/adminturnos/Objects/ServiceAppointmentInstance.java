package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.ObjectInterfaces.ServiceInstance;

import java.util.Calendar;

public class ServiceAppointmentInstance implements ServiceInstance {

    private String id;
    private Calendar date;

    private Service service;

    public ServiceAppointmentInstance(String id, Calendar date, Service service) {
        this.id = id;
        this.date = date;
        this.service = service;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Calendar getDateTime() {
        return date;
    }

    @Override
    public Service getService() {
        return service;
    }
}
