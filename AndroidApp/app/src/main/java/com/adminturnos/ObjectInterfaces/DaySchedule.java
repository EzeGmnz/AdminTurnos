package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public interface DaySchedule extends Serializable {

    int getDayOfWeek();

    String getId();

    Calendar getDayStart();

    Calendar getDayEnd();

    Calendar getPauseStart();

    Calendar getPauseEnd();

    boolean hasPause();

    List<Provides> getProvides();

    Provides getProvidedService(String id);

}
