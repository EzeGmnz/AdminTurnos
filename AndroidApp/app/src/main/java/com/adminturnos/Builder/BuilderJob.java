package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.Objects.NormalJob;
import com.adminturnos.Objects.Place;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderJob implements ObjectBuilder<Job> {

    @Override
    public Job build(JSONObject json) throws JSONException {
        // {"id":1,"place":{"id":1,"street":"paraguay","streetnumber":"555","businessname":"lo del s"}}
        JSONObject jsonPlace = json.getJSONObject("place");
        Place place = new Place(
                jsonPlace.getString("id"),
                jsonPlace.getString("businessname"),
                jsonPlace.getString("street") + " " + jsonPlace.getString("streetnumber")
        );

        Job job = new NormalJob(json.getString("id"), place);
        return job;
    }
}
