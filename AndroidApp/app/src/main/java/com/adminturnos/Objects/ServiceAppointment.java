package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.ServiceInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class ServiceAppointment implements Appointment {

    private List<ServiceInstance> serviceInstanceList;
    private Calendar date;
    private String id;

    public ServiceAppointment(String id, Calendar date) {
        this.id = id;
        this.date = date;
        this.serviceInstanceList = new ArrayList<>();
    }

    public List<ServiceInstance> getServiceInstanceList() {
        return serviceInstanceList;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<ServiceInstance> getServices() {
        return serviceInstanceList;
    }

    @Override
    public void addService(ServiceInstance serviceInstance) {
        this.serviceInstanceList.add(serviceInstance);
    }

    @Override
    public Calendar getDate() {
        return date;
    }
}