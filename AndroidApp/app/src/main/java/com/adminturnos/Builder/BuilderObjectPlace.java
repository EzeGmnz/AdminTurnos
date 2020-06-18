package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.NormalPlace;
import com.adminturnos.Objects.ServiceProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectPlace implements ObjectBuilder<Place> {

    @Override
    public Place build(JSONObject json) throws JSONException {

        Place out;

        JSONObject jsonOwner = json.getJSONObject("serviceprovider");
        ServiceProvider serviceProvider = new BuilderObjectServiceProvider().build(jsonOwner);
        out = new NormalPlace(
                json.getString("id"),
                serviceProvider,
                json.getString("businessname"),
                json.getString("street") + " " + json.getString("streetnumber"));

        return out;
    }

}
