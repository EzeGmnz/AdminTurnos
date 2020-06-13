package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;

/**
 *
 */
public interface Job extends Serializable {

    String getId();

    Place getPlace();
}