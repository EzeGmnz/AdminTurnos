package com.adminturnos.Builder;


import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.Objects.NormalJob;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuilderObjectJob implements ObjectBuilder<Job> {

    @Override
    public Job build(JSONObject json) throws JSONException {
        Job out;

        JSONObject jsonJob = json.getJSONObject("job");
        JSONObject jsonDaySchedulesList = json.getJSONObject("day_schedules");

        JSONObject jsonPlace = jsonJob.getJSONObject("place");
        Place place = new BuilderObjectPlace().build(jsonPlace);

        List<DaySchedule> dayScheduleList = new ArrayList<>();

        DaySchedule daySchedule;
        for (Iterator<String> it = jsonDaySchedulesList.keys(); it.hasNext(); ) {
            String dayOfWeek = it.next();
            JSONObject jsonDaySchedule = jsonDaySchedulesList.getJSONObject(dayOfWeek);
            daySchedule = new BuilderObjectDayScheduleNormal().build(jsonDaySchedule);
            dayScheduleList.add(daySchedule);
        }

        out = new NormalJob(
                jsonJob.getString("id"),
                place,
                dayScheduleList
        );

        return out;
    }
}
