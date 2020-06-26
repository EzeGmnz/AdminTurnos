package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;
import java.util.Calendar;

public interface Provides extends Serializable {

    String getId();

    Service getService();

    float getPrice();

    Calendar getDuration();

    int getParallelism();

    void setPrice(float cost);

    Provides clone();

    void setDuration(Calendar c);

    void setParallelism(int parallelism);
}
