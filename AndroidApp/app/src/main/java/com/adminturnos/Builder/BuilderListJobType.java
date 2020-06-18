package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.JobType;
import com.adminturnos.Objects.JobTypeNormal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuilderListJobType {

    public List<JobType> build(JSONObject response) throws JSONException {

        List<JobType> out = new ArrayList<>();
        JSONArray jobtypesArray = (JSONArray) response.get("jobtypes");

        JobType jobtype;
        for (int i = 0; i < jobtypesArray.length(); i++) {
            jobtype = new JobTypeNormal(jobtypesArray.getString(i));
            out.add(jobtype);
        }

        return out;
    }
}
