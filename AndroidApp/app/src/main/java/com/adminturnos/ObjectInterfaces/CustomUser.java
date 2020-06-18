package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;

/**
 *
 */
public interface CustomUser extends Serializable {

    String getGivenName();

    String getFamilyName();

    String getName();

    String getId();

    String getEmail();

}