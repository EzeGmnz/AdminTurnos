package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuilderListPlace {

    public List<Place> build(JSONObject json) throws JSONException {

        List<Place> out = new ArrayList<>();
        JSONArray places = (JSONArray) json.get("places");

        Place place;
        JSONObject jsonPlace;
        for (int i = 0; i < places.length(); i++) {
            jsonPlace = places.getJSONObject(i);
            place = new BuilderObjectPlace().build(jsonPlace);

            out.add(place);
        }

        return out;
    }

}
