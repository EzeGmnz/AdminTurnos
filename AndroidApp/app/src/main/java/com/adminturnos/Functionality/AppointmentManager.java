package com.adminturnos.Functionality;

import com.adminturnos.ObjectInterfaces.Appointment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public interface AppointmentManager extends Serializable {

    List<Appointment> getAppointmentsInDate(Calendar date);

    void setAppointmentList(List<Appointment> appointmentList);

    String getJobId();
}
