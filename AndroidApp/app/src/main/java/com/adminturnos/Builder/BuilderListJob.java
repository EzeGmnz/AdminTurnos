package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.Job;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuilderListJob {

    public List<Job> build(JSONObject json) throws JSONException {

        JSONObject jsonJobs = json.getJSONObject("jobs");
        List<Job> out = new ArrayList<>();

        Job job;
        JSONObject jsonJob;

        for (Iterator<String> it = jsonJobs.keys(); it.hasNext(); ) {
            jsonJob = jsonJobs.getJSONObject(it.next());

            job = new BuilderObjectJob().build(jsonJob);

            out.add(job);
        }

        return out;
    }
}