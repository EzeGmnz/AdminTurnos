package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.Service;

import java.util.Calendar;

public class ProvidesNormal implements Provides {

    private String id;
    private Service service;
    private float cost;
    private Calendar duration;
    private int parallelism;

    public ProvidesNormal(String id, Service service, float cost, Calendar duration, int parallelism) {
        this.id = id;
        this.service = service;
        this.cost = cost;
        this.duration = duration;
        this.parallelism = parallelism;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Calendar getDuration() {
        return duration;
    }

    @Override
    public int getParallelism() {
        return parallelism;
    }
}
