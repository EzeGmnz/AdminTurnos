package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Service;

/**
 *
 */
public class ServiceNamed implements Service {

    private String jobtype;
    private String id;
    private String name;

    public ServiceNamed(String id, String jobtype, String name) {
        this.jobtype = jobtype;
        this.id = id;
        this.name = name;
    }

    @Override
    public String getJobType() {
        return jobtype;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}