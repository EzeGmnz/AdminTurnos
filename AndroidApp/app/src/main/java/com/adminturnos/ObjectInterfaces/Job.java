package com.adminturnos.ObjectInterfaces;

import com.adminturnos.Functionality.AppointmentManager;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public interface Job extends Serializable, Cloneable {

    String getId();

    Place getPlace();

    List<DaySchedule> getDaySchedules();

    DaySchedule getDaySchedule(int dayOfWeek);

    void addDaySchedule(DaySchedule daySchedule);

    Job clone();

    AppointmentManager getAppointmentManager();

    void setAppointmentManager(AppointmentManager appointmentManager);
}