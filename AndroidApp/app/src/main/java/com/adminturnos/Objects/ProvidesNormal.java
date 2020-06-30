package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.Service;

import java.util.Calendar;
import java.util.Objects;

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
    public float getPrice() {
        return cost;
    }

    @Override
    public void setPrice(float cost) {
        this.cost = cost;
    }

    @Override
    public Calendar getDuration() {
        return duration;
    }

    @Override
    public void setDuration(Calendar duration) {
        this.duration = duration;
    }

    @Override
    public int getParallelism() {
        return parallelism;
    }

    @Override
    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    @Override
    public ProvidesNormal clone() {
        return new ProvidesNormal(id, service, cost, (Calendar) duration.clone(), parallelism);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProvidesNormal that = (ProvidesNormal) o;
        return Float.compare(that.cost, cost) == 0 &&
                parallelism == that.parallelism &&
                Objects.equals(id, that.id) &&
                Objects.equals(service, that.service) &&
                calendarEquals(duration, that.duration);
    }


    public boolean calendarEquals(Calendar c1, Calendar c2) {
        return c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY) &&
                c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
    }
}
