package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.JobType;

/**
 *
 */
public class JobTypeNormal implements JobType {

    private String type;

    /**
     * Default constructor
     */
    public JobTypeNormal(String type) {
        this.type = type;
    }


    @Override
    public String getType() {
        return this.type;
    }
}