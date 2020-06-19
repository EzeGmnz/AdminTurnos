package com.adminturnos.CustomViews;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adminturnos.ObjectInterfaces.Appointment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class CalendarDay {

    protected Calendar calendar;
    protected List<Appointment> appointmentList;

    public CalendarDay(Calendar calendar) {
        this.calendar = calendar;
        this.appointmentList = new ArrayList<>();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public abstract void populateView(ViewGroup view, LayoutInflater inflater);

}
