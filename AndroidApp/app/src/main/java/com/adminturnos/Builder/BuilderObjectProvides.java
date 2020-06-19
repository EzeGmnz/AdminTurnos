package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.Objects.ProvidesNormal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BuilderObjectProvides implements ObjectBuilder<Provides> {
    @Override
    public Provides build(JSONObject json) throws JSONException {

        Provides provides = null;

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            Calendar duration = Calendar.getInstance();
            duration.setTime(dateTimeFormat.parse(json.getString("duration")));

            Service service = new BuilderObjectService().build(json.getJSONObject("service"));

            provides = new ProvidesNormal(
                    json.getString("id"),
                    service,
                    (float) json.getDouble("cost"),
                    duration,
                    json.getInt("parallelism")
            );

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return provides;
    }
}
