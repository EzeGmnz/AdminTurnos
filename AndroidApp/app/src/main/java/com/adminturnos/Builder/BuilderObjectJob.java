package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.NormalJob;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectJob implements ObjectBuilder<Job> {

    @Override
    public Job build(JSONObject json) throws JSONException {

        Job out;

        JSONObject jsonPlace = json.getJSONObject("place");
        Place place = new BuilderObjectPlace().build(jsonPlace);

        out = new NormalJob(
                json.getString("id"),
                place
        );

        return out;
    }
}
