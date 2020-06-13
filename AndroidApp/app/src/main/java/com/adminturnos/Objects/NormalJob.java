package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;

/**
 *
 */
public class NormalJob implements Job {
    private String id;
    private Place place;

    public NormalJob(String id, Place place) {
        this.id = id;
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public Place getPlace() {
        return place;
    }

}