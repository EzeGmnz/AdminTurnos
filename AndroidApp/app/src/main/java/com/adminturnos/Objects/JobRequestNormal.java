package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.JobRequest;
import com.adminturnos.ObjectInterfaces.Place;

public class JobRequestNormal implements JobRequest {

    private Place place;
    private CustomUser customUser;

    public JobRequestNormal(Place place, CustomUser customUser) {
        this.place = place;
        this.customUser = customUser;
    }

    @Override
    public Place getPlace() {
        return place;
    }

    @Override
    public CustomUser getCustomUser() {
        return customUser;
    }

}
