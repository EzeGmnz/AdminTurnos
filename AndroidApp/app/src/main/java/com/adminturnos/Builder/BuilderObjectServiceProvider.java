package com.adminturnos.Builder;

import com.adminturnos.Objects.ServiceProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectServiceProvider implements ObjectBuilder<ServiceProvider> {

    @Override
    public ServiceProvider build(JSONObject json) throws JSONException {

        ServiceProvider out;

        out = new ServiceProvider(
                json.getString("id"),
                json.getString("given_name"),
                json.getString("family_name"),
                json.getString("email")

        );

        return out;
    }
}
