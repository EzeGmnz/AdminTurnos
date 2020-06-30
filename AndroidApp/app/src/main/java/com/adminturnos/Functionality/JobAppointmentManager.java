package com.adminturnos.Functionality;

import com.adminturnos.ObjectInterfaces.Appointment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JobAppointmentManager implements AppointmentManager {

    private final String jobId;
    private List<Appointment> appointmentList;

    public JobAppointmentManager(String jobId) {
        this.jobId = jobId;
        this.appointmentList = new ArrayList<>();
    }

    @Override
    public List<Appointment> getAppointmentsInDate(Calendar date) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : appointmentList) {
            boolean sameDay = a.getDate().get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) &&
                    a.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR);
            if (sameDay) {
                out.add(a);
            }
        }
        out.sort(new AppointmentComparator());

        return out;
    }

    @Override
    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    private static class AppointmentComparator implements java.util.Comparator<Appointment> {

        @Override
        public int compare(Appointment o1, Appointment o2) {
            return o1.getServiceInstances().get(0).getDateTime().compareTo(o2.getServiceInstances().get(0).getDateTime());
        }
    }
}