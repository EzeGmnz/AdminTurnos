package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Service;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceNamed that = (ServiceNamed) o;
        return Objects.equals(jobtype, that.jobtype) &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

}