package com.adminturnos.Activities.Job;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adminturnos.Functionality.AppointmentManager;
import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.ServiceInstance;
import com.adminturnos.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDailyView extends Fragment {

    private static final Map<Integer, String> mapNumberDay = new HashMap<Integer, String>() {{
        put(1, "Dom");
        put(2, "Lun");
        put(3, "Mar");
        put(4, "Mie");
        put(5, "Jue");
        put(6, "Vie");
        put(7, "Sab");
    }};

    private Calendar currentDay;
    private Job job;
    private LinearLayout timesContainer, containerServiceSeparation, serviceNameContainer, timeLineContainer;
    private AppointmentManager appointmentManager;
    private Map<String, ViewGroup> mapServiceViewGroup;
    private Map<View, Appointment> mapAppointmentViewGroup;

    public FragmentDailyView(Job job) {
        this.currentDay = Calendar.getInstance();
        this.mapServiceViewGroup = new HashMap<>();
        this.mapAppointmentViewGroup = new HashMap<>();
        this.job = job;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_daily_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.timesContainer = view.findViewById(R.id.timesContainer);
        this.containerServiceSeparation = view.findViewById(R.id.containerServiceSeparation);
        this.serviceNameContainer = view.findViewById(R.id.serviceNameContainer);
        this.timeLineContainer = view.findViewById(R.id.timeLineContainer);
        appointmentManager = job.getAppointmentManager();
        populateDay();
    }

    public void setDay(Calendar day) {
        this.currentDay = day;
        populateDay();
    }

    private void clearContainers() {
        this.timesContainer.removeAllViews();
        this.containerServiceSeparation.removeAllViews();
        this.serviceNameContainer.removeAllViews();
        this.timeLineContainer.removeAllViews();
    }

    private void populateDay() {
        clearContainers();

        TextView textViewDayNumberIndicator = getView().findViewById(R.id.textViewDayNumberIndicator);
        TextView textViewDayIndicator = getView().findViewById(R.id.textViewDayIndicator);

        textViewDayNumberIndicator.setText(currentDay.get(Calendar.DATE) + "/" + (1 + currentDay.get(Calendar.MONTH)));
        textViewDayIndicator.setText(mapNumberDay.get(currentDay.get(Calendar.DAY_OF_WEEK)));

        if (hasWorkThisDay()) {
            displayHasWorkThisDay();
            populateTimesContainer();
            populateServiceSeparations();
            populateAppointments();
        } else {
            displayNoWorkThisDay();
        }
    }

    private boolean hasWorkThisDay() {
        DaySchedule daySchedule = job.getDaySchedule(currentDay.get(Calendar.DAY_OF_WEEK));
        return daySchedule != null;
    }

    private void displayHasWorkThisDay() {
        getView().findViewById(R.id.holderdayindicator).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.scrollViewDaily).setVisibility(View.VISIBLE);
        serviceNameContainer.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.noWorkTodayContainer).setVisibility(View.GONE);
    }

    private void displayNoWorkThisDay() {
        //TODO
        serviceNameContainer.setVisibility(View.GONE);
        getView().findViewById(R.id.holderdayindicator).setVisibility(View.GONE);
        getView().findViewById(R.id.scrollViewDaily).setVisibility(View.GONE);
        getView().findViewById(R.id.noWorkTodayContainer).setVisibility(View.VISIBLE);

        TextView textViewDayNumberIndicator = getView().findViewById(R.id.textViewDayNumberIndicatorNoWork);
        TextView textViewDayIndicator = getView().findViewById(R.id.textViewDayIndicatorNoWork);

        textViewDayNumberIndicator.setText(currentDay.get(Calendar.DATE) + "");
        textViewDayIndicator.setText(mapNumberDay.get(currentDay.get(Calendar.DAY_OF_WEEK)));
    }

    private void populateTimesContainer() {

        DaySchedule daySchedule = job.getDaySchedule(currentDay.get(Calendar.DAY_OF_WEEK));
        Calendar currentTime = (Calendar) daySchedule.getDayStart().clone();
        Calendar endTime = daySchedule.getDayEnd();

        while (currentTime.get(Calendar.HOUR_OF_DAY) < endTime.get(Calendar.HOUR_OF_DAY)) {
            newTimeView(currentTime);
            newTimeSeparator();
            currentTime.add(Calendar.MINUTE, 30);
        }

    }

    private void newTimeView(Calendar time) {
        View view = getLayoutInflater().inflate(R.layout.daily_hour_minute, timesContainer, false);

        TextView textViewTime = view.findViewById(R.id.textViewTime);
        String timeStr = String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        textViewTime.setText(timeStr);

        timesContainer.addView(view);
    }

    private void newTimeSeparator() {
        View view = getLayoutInflater().inflate(R.layout.daily_time_line, timeLineContainer, false);
        timeLineContainer.addView(view);
    }

    private void populateServiceSeparations() {
        DaySchedule daySchedule = job.getDaySchedule(currentDay.get(Calendar.DAY_OF_WEEK));
        List<Provides> provides = daySchedule.getProvides();
        int cantServices = provides.size();
        for (Provides p : provides) {
            newServiceSeparationView(p.getService().getId(), cantServices);
            newServiceNameView(p.getService().getName(), cantServices);
        }
    }

    private void newServiceNameView(String name, int cantDivisions) {

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.daily_service_name, serviceNameContainer, false);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.weight = 1f / cantDivisions;

        TextView serviceName = linearLayout.findViewById(R.id.textViewServiceName);
        serviceName.setText(name);

        linearLayout.setLayoutParams(lp);
        serviceNameContainer.addView(linearLayout);
    }

    private void newServiceSeparationView(String serviceId, int cantDivisions) {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.daily_service_separation, containerServiceSeparation, false);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.weight = 1f / cantDivisions;

        mapServiceViewGroup.put(serviceId, view);
        containerServiceSeparation.addView(view);
    }

    private void populateAppointments() {
        for (Appointment a : appointmentManager.getAppointmentsInDate(currentDay)) {
            for (ServiceInstance si : a.getServiceInstances()) {
                newServiceInstanceView(a, si);
            }
        }
    }

    private void newServiceInstanceView(final Appointment a, ServiceInstance si) {
        ViewGroup correspServiceSeparation = mapServiceViewGroup.get(si.getService().getId());
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.daily_appointment_layout, correspServiceSeparation, false);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();

        lp.topMargin = getMarginTopPixels(si);
        lp.height = getHeightPixels(si);

        TextView textViewAppointmentTime = view.findViewById(R.id.textViewAppointmentTime);
        TextView textViewClient = view.findViewById(R.id.textViewClient);

        mapAppointmentViewGroup.put(view, a);

        view.setOnClickListener(new ListenerAppointmentClick());

        String timeStr = String.format("%02d:%02d", si.getDateTime().get(Calendar.HOUR_OF_DAY), si.getDateTime().get(Calendar.MINUTE));
        textViewAppointmentTime.setText(timeStr);
        textViewClient.setText(a.getClient().getName());

        correspServiceSeparation.addView(view);
    }

    private int getHeightPixels(ServiceInstance si) {

        Calendar end = (Calendar) findDuration(si).clone();
        Calendar start = si.getDateTime();
        end.add(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
        end.add(Calendar.MINUTE, start.get(Calendar.MINUTE));
        int timeSeparationHalfHourPx = getResources().getDimensionPixelSize(R.dimen.half_hour);
        return timeSeparationHalfHourPx * getCantHalfHoursBetween(start, end);
    }

    private Calendar findDuration(ServiceInstance si) {
        for (Provides p : job.getDaySchedule(currentDay.get(Calendar.DAY_OF_WEEK)).getProvides()) {
            if (p.getService().getId().equals(si.getService().getId())) {
                return p.getDuration();
            }
        }
        return null;
    }

    private int getMarginTopPixels(ServiceInstance si) {

        int timeSeparationHalfHourPx = getResources().getDimensionPixelSize(R.dimen.half_hour);
        DaySchedule daySchedule = job.getDaySchedule(currentDay.get(Calendar.DAY_OF_WEEK));

        return timeSeparationHalfHourPx * getCantHalfHoursBetween(daySchedule.getDayStart(), si.getDateTime());
    }

    private int getCantHalfHoursBetween(Calendar start, Calendar end) {
        int out = 0;
        start = (Calendar) start.clone();
        boolean cont = true;
        while (cont) {
            out++;
            start.add(Calendar.MINUTE, 30);
            cont = start.get(Calendar.HOUR_OF_DAY) < end.get(Calendar.HOUR_OF_DAY) ||
                    start.get(Calendar.HOUR_OF_DAY) == end.get(Calendar.HOUR_OF_DAY) &&
                            start.get(Calendar.MINUTE) < end.get(Calendar.MINUTE);
        }

        return out;
    }

    private class ListenerAppointmentClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ViewAppointmentActivity.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("appointment", mapAppointmentViewGroup.get(v));
            bundle.putSerializable("job", job);

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}