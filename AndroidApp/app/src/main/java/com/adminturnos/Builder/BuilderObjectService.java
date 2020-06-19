package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.Objects.ServiceNamed;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectService implements ObjectBuilder<Service> {
    @Override
    public Service build(JSONObject json) throws JSONException {

        String serviceName = json.getString("name");
        String serviceJobType = json.getString("jobtype");
        String id = json.getString("id");

        return new ServiceNamed(id, serviceJobType, serviceName);
    }
}
