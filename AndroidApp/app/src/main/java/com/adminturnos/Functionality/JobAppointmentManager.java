package com.adminturnos.Functionality;

import com.adminturnos.Builder.BuilderListAppointment;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Listeners.ListenerAppointmentHolder;
import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class JobAppointmentManager implements AppointmentManager {

    private final String jobId;
    private ListenerAppointmentHolder listener;

    private List<Appointment> appointments;

    public JobAppointmentManager(String jobId, ListenerAppointmentHolder listener) {
        this.jobId = jobId;
        this.listener = listener;
        fetchAppointments();
    }

    private void fetchAppointments() {
        Map<String, String> body = new HashMap<>();
        body.put("job_id", "" + jobId);

        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_GET_APPOINTMENTS,
                body,
                new CallbackGetAppointments()
        );
    }

    private void notifyListener() {
        if (this.listener != null) {
            this.listener.onFetch(this);
        }
    }

    private void populateAppointments(JSONObject response) {

        try {
            appointments = new BuilderListAppointment().build(response);
            appointments.sort(new AppointmentComparator());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public List<Appointment> getAppointmentsInDate(Calendar date) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : appointments) {
            boolean sameDay = a.getDate().get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) &&
                    a.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR);
            if (sameDay) {
                out.add(a);
            }
        }

        return out;
    }

    private class CallbackGetAppointments extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            populateAppointments(response);
            notifyListener();
        }
    }

    private class AppointmentComparator implements java.util.Comparator<Appointment> {

        @Override
        public int compare(Appointment o1, Appointment o2) {
            return o1.getServiceInstances().get(0).getDateTime().compareTo(o2.getServiceInstances().get(0).getDateTime());
        }
    }
}