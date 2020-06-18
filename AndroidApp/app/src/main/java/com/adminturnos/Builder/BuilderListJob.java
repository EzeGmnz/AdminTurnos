package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuilderListJob {

    public List<Job> build(JSONObject json) throws JSONException {

        JSONArray jobs = (JSONArray) json.get("jobs");
        List<Job> out = new ArrayList<>();

        Job job;
        JSONObject jsonJob;

        for (int i = 0; i < jobs.length(); i++) {
            jsonJob = jobs.getJSONObject(i);
            job = new BuilderObjectJob().build(jsonJob);

            out.add(job);
        }

        return out;
    }
}
