package com.adminturnos.Functionality;

import com.adminturnos.Listeners.ListenerAppointmentHolder;

import java.util.HashMap;
import java.util.Map;

public class JobAppointmentHolderWrapper {

    private static JobAppointmentHolderWrapper instance;
    private Map<String, AppointmentManager> appointmentManagerList;

    private JobAppointmentHolderWrapper() {
        this.appointmentManagerList = new HashMap<>();
    }

    public static JobAppointmentHolderWrapper getInstance() {
        if (instance == null) {
            instance = new JobAppointmentHolderWrapper();
        }
        return instance;
    }

    private void registerAppointmentManager(String jobId, AppointmentManager manager) {
        this.appointmentManagerList.put(jobId, manager);
    }

    public void getAppointmentManager(String jobId, ListenerAppointmentHolder listener) {
        if (!appointmentManagerList.containsKey(jobId)) {
            createAppointmentManager(jobId, listener);
        } else {
            AppointmentManager manager = appointmentManagerList.get(jobId);
            if (listener != null) {
                listener.onFetch(manager);
            }
        }
    }

    private void createAppointmentManager(String jobId, ListenerAppointmentHolder listener) {
        AppointmentManager appointmentManager = new JobAppointmentManager(jobId, listener);
        registerAppointmentManager(jobId, appointmentManager);
    }

    private void retrieveFromStorage() {
        //TODO
    }

}
