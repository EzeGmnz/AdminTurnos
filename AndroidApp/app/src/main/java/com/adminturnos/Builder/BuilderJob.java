package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.NormalJob;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderJob implements ObjectBuilder<Job> {

    @Override
    public Job build(JSONObject json) throws JSONException {
        // {"id":1,"place":{"id":1,"street":"paraguay","streetnumber":"555","businessname":"lo del s"}}
        JSONObject jsonPlace = json.getJSONObject("place");

        Place place = new BuilderPlace().build(jsonPlace);

        return new NormalJob(json.getString("id"), place);
    }
}
