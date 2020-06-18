package com.adminturnos.ObjectInterfaces;

import java.io.Serializable;

public interface JobRequest extends Serializable {

    Place getPlace();

    CustomUser getCustomUser();

}
