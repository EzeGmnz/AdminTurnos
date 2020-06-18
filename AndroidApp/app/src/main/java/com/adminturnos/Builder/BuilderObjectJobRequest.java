package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.JobRequest;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.JobRequestNormal;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectJobRequest implements ObjectBuilder<JobRequest> {

    @Override
    public JobRequest build(JSONObject json) throws JSONException {

        JobRequest out;

        JSONObject jsonPlace = json.getJSONObject("place");
        JSONObject jsonServiceProvider = json.getJSONObject("serviceprovider");

        Place place = new BuilderObjectPlace().build(jsonPlace);
        CustomUser serviceProvider = new BuilderObjectServiceProvider().build(jsonServiceProvider);

        out = new JobRequestNormal(place, serviceProvider);

        return out;
    }
}
