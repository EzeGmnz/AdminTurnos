package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.NormalPlace;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderPlace implements ObjectBuilder<Place> {

    @Override
    public Place build(JSONObject json) throws JSONException {

        return new NormalPlace(
                json.getString("id"),
                json.getString("businessname"),
                json.getString("street") + " " + json.getString("streetnumber")// + " " + json.getString("apnumber")
        );
    }

}
