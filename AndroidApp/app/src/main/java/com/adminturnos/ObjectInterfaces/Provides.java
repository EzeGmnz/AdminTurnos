package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;
import java.util.Calendar;

public interface Provides extends Serializable {

    String getId();

    Service getService();

    float getCost();

    Calendar getDuration();

    int getParallelism();


}
