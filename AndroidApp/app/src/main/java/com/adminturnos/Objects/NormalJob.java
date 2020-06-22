package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;

import java.util.List;

/**
 *
 */
public class NormalJob implements Job {
    private String id;
    private Place place;
    private List<DaySchedule> dayScheduleList;

    public NormalJob(String id, Place place, List<DaySchedule> daySchedules) {
        this.dayScheduleList = daySchedules;
        this.id = id;
        this.place = place;
    }

    public void setDayScheduleList(List<DaySchedule> dayScheduleList) {
        this.dayScheduleList = dayScheduleList;
    }

    public String getId() {
        return id;
    }

    public Place getPlace() {
        return place;
    }

    @Override
    public List<DaySchedule> getDaySchedules() {
        return dayScheduleList;
    }

    @Override
    public DaySchedule getDaySchedule(int dayOfWeek) {
        for (DaySchedule ds : dayScheduleList) {
            if (ds.getDayOfWeek() == dayOfWeek) {
                return ds;
            }
        }

        return null;
    }

}