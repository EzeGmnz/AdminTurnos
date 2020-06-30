package com.adminturnos.Database;

import com.adminturnos.Builder.BuilderListAppointment;
import com.adminturnos.Builder.BuilderListJob;
import com.adminturnos.Functionality.AppointmentManager;
import com.adminturnos.Functionality.JobAppointmentManager;
import com.adminturnos.Listeners.DatabaseCallback;
import com.adminturnos.Listeners.RepositoryGetJobListListener;
import com.adminturnos.Listeners.RepositoryGetJobListener;
import com.adminturnos.Listeners.RepositorySaveListener;
import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.Values;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class JobRepositoryManagerRemote implements JobRepositoryManager {

    private static JobRepositoryManagerRemote instance;
    private List<Job> jobList;

    private JobRepositoryManagerRemote() {

    }

    public static JobRepositoryManagerRemote getInstance() {
        if (instance == null) {
            instance = new JobRepositoryManagerRemote();
        }
        return instance;
    }

    @Override
    public void invalidate() {
        jobList = null;
    }

    @Override
    public void saveJobToStorage(String jobId) {
        saveDaySchedulesForJob(getJobForId(jobId));
    }

    @Override
    public void getJobs(RepositoryGetJobListListener listener) {
        if (jobList != null) {
            listener.onFetch(jobList);
        } else {
            fetchJobs(listener);
        }
    }

    @Override
    public void saveJob(Job job, RepositorySaveListener listener) {
        Job toReplace = getJobForId(job.getId());
        if (toReplace != null) {
            jobList.remove(toReplace);
        }
        jobList.add(job);

        if (listener != null) {
            listener.onSuccess();
        }
    }

    @Override
    public void getJob(String id, RepositoryGetJobListener listener) {
        Job out = getJobForId(id);
        listener.onFetch(out);
    }

    private void saveDaySchedulesForJob(Job job) {
        JSONObject body = new JSONObject();

        try {
            body.put("job_id", job.getId());

            JSONObject days = new JSONObject();
            for (DaySchedule d : job.getDaySchedules()) {
                String dayStart = d.getDayStart().get(Calendar.HOUR_OF_DAY) + ":" + d.getDayStart().get(Calendar.MINUTE) + ":00";
                String dayEnd = d.getDayEnd().get(Calendar.HOUR_OF_DAY) + ":" + d.getDayEnd().get(Calendar.MINUTE) + ":00";

                String pauseEnd = null, pauseStart = null;
                if (d.getPauseStart() != null) {
                    pauseStart = d.getPauseStart().get(Calendar.HOUR_OF_DAY) + ":" + d.getPauseStart().get(Calendar.MINUTE) + ":00";
                    pauseEnd = d.getPauseEnd().get(Calendar.HOUR_OF_DAY) + ":" + d.getPauseEnd().get(Calendar.MINUTE) + ":00";
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("day_start", dayStart);
                jsonObject.put("day_end", dayEnd);
                jsonObject.put("pause_start", pauseStart);
                jsonObject.put("pause_end", pauseEnd);
                days.put(d.getDayOfWeek() + "", jsonObject);
            }
            body.put("days", days);

            DatabaseDjangoWrite.getInstance().POSTJSON(
                    Values.DJANGO_URL_NEW_DAY_SCHEDULE,
                    body,
                    new CallbackSaveDayScheduleForJob(job)
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveProvidesForJob(Job job) {
        try {
            for (DaySchedule ds : job.getDaySchedules()) {
                JSONObject body = new JSONObject();
                body.put("job_id", job.getId());

                body.put("day_of_week", ds.getDayOfWeek());
                JSONArray servicesArray = new JSONArray();
                JSONObject costsJson = new JSONObject();
                JSONObject durationsJson = new JSONObject();
                JSONObject parallelismsJson = new JSONObject();

                for (Provides p : ds.getProvides()) {
                    String duration = p.getDuration().get(Calendar.HOUR_OF_DAY) + ":" + p.getDuration().get(Calendar.MINUTE) + ":00";
                    servicesArray.put(p.getService().getId());
                    costsJson.put(p.getService().getId(), p.getPrice());
                    durationsJson.put(p.getService().getId(), duration);
                    parallelismsJson.put(p.getService().getId(), p.getParallelism());
                }

                body.put("services", servicesArray);
                body.put("costs", costsJson);
                body.put("durations", durationsJson);
                body.put("parallelisms", parallelismsJson);

                DatabaseDjangoWrite.getInstance().POSTJSON(
                        Values.DJANGO_URL_NEW_PROVIDES,
                        body,
                        new DatabaseCallback() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // TODO
                            }
                        }
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Job getJobForId(String jobId) {
        for (Job j : jobList) {
            if (j.getId().equals(jobId)) {
                return j;
            }
        }
        return null;
    }

    private void fetchJobs(RepositoryGetJobListListener listener) {
        DatabaseDjangoRead.getInstance().GET(Values.DJANGO_URL_GET_JOBS, null, new CallbackGetJobs(listener));
    }

    private void fetchAppointmentManagerForJob(String jobId) {
        Map<String, String> body = new HashMap<>();
        body.put("job_id", "" + jobId);

        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_GET_APPOINTMENTS,
                body,
                new CallbackGetAppointmentsForJob(jobId)
        );
    }

    private void notifyListenerGetJobs(List<Job> jobList, RepositoryGetJobListListener listener) {

        this.jobList = jobList;
        listener.onFetch(jobList);

        for (Job j : jobList) {
            fetchAppointmentManagerForJob(j.getId());
        }

    }

    private void populateAppointmentManager(String jobId, List<Appointment> appointments) {
        AppointmentManager appointmentManager = new JobAppointmentManager(jobId);
        appointmentManager.setAppointmentList(appointments);
        getJobForId(jobId).setAppointmentManager(appointmentManager);
    }

    private class CallbackGetJobs extends DatabaseCallback {

        private final RepositoryGetJobListListener listener;

        public CallbackGetJobs(RepositoryGetJobListListener listener) {
            this.listener = listener;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                notifyListenerGetJobs(new BuilderListJob().build(response), listener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CallbackGetAppointmentsForJob extends DatabaseCallback {

        private String job_id;

        public CallbackGetAppointmentsForJob(String job_id) {
            this.job_id = job_id;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                populateAppointmentManager(job_id, new BuilderListAppointment().build(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CallbackSaveDayScheduleForJob extends DatabaseCallback {

        private final Job job;

        public CallbackSaveDayScheduleForJob(Job job) {
            this.job = job;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            saveProvidesForJob(job);
        }
    }
}
