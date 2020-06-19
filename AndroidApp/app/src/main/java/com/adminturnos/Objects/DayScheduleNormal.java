package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Provides;

import java.util.Calendar;
import java.util.List;

public class DayScheduleNormal implements DaySchedule {

    private Calendar dayStart;
    private Calendar dayEnd;
    private Calendar pauseStart;
    private Calendar pauseEnd;
    private String id;
    private List<Provides> providesList;
    private int dayOfWeek;

    public DayScheduleNormal(String id, int dayOfWeek, Calendar dayStart, Calendar dayEnd, Calendar pauseStart, Calendar pauseEnd, List<Provides> providesList) {
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
        this.pauseStart = pauseStart;
        this.pauseEnd = pauseEnd;
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.providesList = providesList;
    }

    @Override
    public Calendar getDayStart() {
        return dayStart;
    }

    @Override
    public Calendar getDayEnd() {
        return dayEnd;
    }

    @Override
    public Calendar getPauseStart() {
        return pauseStart;
    }

    @Override
    public Calendar getPauseEnd() {
        return pauseEnd;
    }

    @Override
    public boolean hasPause() {
        return false;
    }

    @Override
    public List<Provides> getProvides() {
        return providesList;
    }

    @Override
    public Provides getProvidedService(String id) {
        for (Provides p : providesList) {
            if (p.getService().getId().equals(id)) {
                return p;
            }
        }

        return null;
    }

    @Override
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public String getId() {
        return id;
    }


}
