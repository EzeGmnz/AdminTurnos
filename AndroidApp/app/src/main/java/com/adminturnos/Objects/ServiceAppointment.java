package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.ServiceInstance;

import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class ServiceAppointment implements Appointment {

    private List<ServiceInstance> serviceInstanceList;
    private Calendar date;
    private CustomUser client;
    private String id;

    public ServiceAppointment(CustomUser client, String id, Calendar date, List<ServiceInstance> list) {
        this.serviceInstanceList = list;
        this.id = id;
        this.client = client;
        this.date = date;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CustomUser getClient() {
        return client;
    }

    @Override
    public List<ServiceInstance> getServiceInstances() {
        return serviceInstanceList;
    }

    @Override
    public void setServiceInstances(List<ServiceInstance> list) {
        this.serviceInstanceList = list;
    }


    @Override
    public Calendar getDate() {
        return date;
    }
}