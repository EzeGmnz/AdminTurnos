package com.adminturnos.Builder;

import com.adminturnos.ObjectViews.ViewScheduleTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BuilderListScheduleTemplate {

    public List<ViewScheduleTemplate> build(JSONObject response) throws JSONException {
        List<ViewScheduleTemplate> out = new ArrayList<>();

        JSONArray arrayScheduleTemplates = response.getJSONArray("schedule_templates");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

        Calendar open, close;
        JSONObject scheduleTemplate;
        for (int i = 0; i < arrayScheduleTemplates.length(); i++) {
            scheduleTemplate = arrayScheduleTemplates.getJSONObject(i);

            try {
                open = Calendar.getInstance();
                open.setTime(dateTimeFormat.parse(scheduleTemplate.getString("open")));

                close = Calendar.getInstance();
                close.setTime(dateTimeFormat.parse(scheduleTemplate.getString("close")));

                JSONArray daysJson = scheduleTemplate.getJSONArray("days");
                String name = scheduleTemplate.getString("name");

                List<Integer> days = new ArrayList<>();
                for (int j = 0; j < daysJson.length(); ++j) {
                    days.add(daysJson.optInt(j));
                }

                out.add(new ViewScheduleTemplate(name, open, close, days));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

}
