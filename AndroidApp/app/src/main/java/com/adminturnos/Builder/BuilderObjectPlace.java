package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.NormalPlace;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectPlace implements ObjectBuilder<Place> {

    @Override
    public Place build(JSONObject json) throws JSONException {

        Place out;

        JSONObject jsonOwner = json.getJSONObject("serviceprovider");
        CustomUser customUserImpl = new BuilderObjectCustomUser().build(jsonOwner);
        out = new NormalPlace(
                json.getString("id"),
                customUserImpl,
                json.getString("businessname"),
                json.getString("street") + " " + json.getString("streetnumber"));

        return out;
    }

}
