package com.adminturnos.Functionality;

import com.adminturnos.ObjectInterfaces.Appointment;

import java.util.Calendar;
import java.util.List;

public interface AppointmentManager {

    List<Appointment> getAppointmentsInDate(Calendar date);

}
