package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.Objects.DayScheduleNormal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BuilderObjectDayScheduleNormal implements ObjectBuilder<DaySchedule> {
    @Override
    public DaySchedule build(JSONObject json) throws JSONException {

        JSONObject jsonDaySchedule = json.getJSONObject("day_schedule");
        JSONArray jsonProvidesList = json.getJSONArray("provides");

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        DaySchedule daySchedule = null;

        Calendar dayStart, dayEnd, pauseStart, pauseEnd;
        try {
            dayStart = Calendar.getInstance();
            dayStart.setTime(dateTimeFormat.parse(jsonDaySchedule.getString("day_start")));

            dayEnd = Calendar.getInstance();
            dayEnd.setTime(dateTimeFormat.parse(jsonDaySchedule.getString("day_end")));

            pauseStart = Calendar.getInstance();
            pauseStart.setTime(dateTimeFormat.parse(jsonDaySchedule.getString("pause_start")));

            pauseEnd = Calendar.getInstance();
            pauseEnd.setTime(dateTimeFormat.parse(jsonDaySchedule.getString("pause_end")));

            List<Provides> providesList = new ArrayList<>();

            ObjectBuilder<Provides> builder = new BuilderObjectProvides();
            for (int i = 0; i < jsonProvidesList.length(); i++) {
                providesList.add(builder.build(jsonProvidesList.getJSONObject(i)));
            }

            daySchedule = new DayScheduleNormal(
                    jsonDaySchedule.getString("id"),
                    jsonDaySchedule.getInt("day_of_week"),
                    dayStart,
                    dayEnd,
                    pauseStart,
                    pauseEnd,
                    providesList
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return daySchedule;
    }
}
