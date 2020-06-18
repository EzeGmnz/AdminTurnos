package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;

/**
 *
 */
public interface Place extends Serializable {

    String getId();

    String getBusinessName();

    String getAddress();

    CustomUser getOwner();
}