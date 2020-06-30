package com.adminturnos.Objects;

import com.adminturnos.Functionality.AppointmentManager;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class NormalJob implements Job {
    private String id;
    private Place place;
    private List<DaySchedule> dayScheduleList;
    private AppointmentManager appointmentManager;

    public NormalJob(String id, Place place, List<DaySchedule> daySchedules) {
        this.dayScheduleList = daySchedules;
        this.id = id;
        this.place = place;
        if (daySchedules == null) {
            this.dayScheduleList = new ArrayList<>();
        }
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

    @Override
    public void addDaySchedule(DaySchedule daySchedule) {
        this.dayScheduleList.add(daySchedule);
    }

    @Override
    public AppointmentManager getAppointmentManager() {
        return appointmentManager;
    }

    @Override
    public void setAppointmentManager(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
    }

    @Override
    public Job clone() {
        Job out = new NormalJob(id, place, null);
        for (DaySchedule ds : dayScheduleList) {
            out.addDaySchedule(ds.clone());
        }
        out.setAppointmentManager(appointmentManager);
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NormalJob normalJob = (NormalJob) o;

        if (getDaySchedules().size() != normalJob.getDaySchedules().size()) return false;

        boolean foundEqualDaySchedule;
        for (DaySchedule ds1 : dayScheduleList) {

            foundEqualDaySchedule = false;
            for (DaySchedule ds2 : normalJob.getDaySchedules()) {
                if (ds1.equals(ds2)) {
                    foundEqualDaySchedule = true;
                    break;
                }
            }

            if (!foundEqualDaySchedule) {
                return false;
            }
        }

        return Objects.equals(id, normalJob.id) &&
                Objects.equals(place, normalJob.place);
    }

}