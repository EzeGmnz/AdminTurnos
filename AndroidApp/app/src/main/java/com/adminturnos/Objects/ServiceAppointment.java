package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.ServiceInstance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class ServiceAppointment implements Appointment {

    private List<ServiceInstance> serviceInstanceList;
    private Calendar date, startTime, endTime;
    private CustomUser client;
    private String id;

    public ServiceAppointment(CustomUser client, String id, Calendar date) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.serviceInstanceList = new ArrayList<>();
    }

    private void getStartEndTime() {
        endTime = serviceInstanceList.get(0).getDateTime();
        startTime = serviceInstanceList.get(0).getDateTime();

        for (ServiceInstance instance : serviceInstanceList) {
            if (instance.getDateTime().compareTo(endTime) > 0) {
                endTime = instance.getDateTime();
            }
            if (instance.getDateTime().compareTo(startTime) < 0) {
                startTime = instance.getDateTime();
            }
        }
    }

    public List<ServiceInstance> getServiceInstanceList() {
        return serviceInstanceList;
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
    public void addService(ServiceInstance serviceInstance) {
        this.serviceInstanceList.add(serviceInstance);
    }

    @Override
    public Calendar getDate() {
        return date;
    }

    @Override
    public Calendar getStartTime() {
        if (startTime == null) {
            getStartEndTime();
        }
        return startTime;
    }

    @Override
    public Calendar getEndTime() {
        if (endTime == null) {
            getStartEndTime();
        }
        return endTime;
    }
}