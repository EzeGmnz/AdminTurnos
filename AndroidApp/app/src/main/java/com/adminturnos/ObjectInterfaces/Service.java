package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;

/**
 *
 */
public interface Service extends Serializable {

    String getJobType();

    String getId();

    String getName();

}