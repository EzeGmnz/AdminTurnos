package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public interface Job extends Serializable {

    String getId();

    Place getPlace();

    List<DaySchedule> getDaySchedules();

    DaySchedule getDaySchedule(int dayOfWeek);
}