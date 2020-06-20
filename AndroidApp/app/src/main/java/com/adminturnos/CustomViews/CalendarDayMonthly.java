package com.adminturnos.CustomViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.R;

import java.util.Calendar;

public class CalendarDayMonthly extends CalendarDay {

    public CalendarDayMonthly(Calendar calendar) {
        super(calendar);
    }

    @Override
    public void populateView(ViewGroup view, LayoutInflater inflater) {
        TextView textViewDayNumber = view.findViewById(R.id.textViewDayNumber);
        textViewDayNumber.setText(calendar.get(Calendar.DATE) + "");

        populateRecyclerView(view, inflater);
    }

    private void populateRecyclerView(ViewGroup view, LayoutInflater inflater) {
        LinearLayout appointmentContainer = view.findViewById(R.id.containerAppointment);
        for (Appointment a : appointmentList) {
            View appointmentView = inflater.inflate(R.layout.monthly_appointment_layout, appointmentContainer, false);
            populateView(a, appointmentView);
            appointmentContainer.addView(appointmentView);
        }
    }

    private void populateView(Appointment a, View appointmentView) {
        TextView textViewAppointmentData = appointmentView.findViewById(R.id.textViewAppointmentData);
        TextView textViewAppointmentTime = appointmentView.findViewById(R.id.textViewAppointmentTime);

        Calendar last = a.getServiceInstances().get(a.getServiceInstances().size() - 1).getDateTime();
        String timeStr = String.format("%02d:%02d", last.get(Calendar.HOUR_OF_DAY), last.get(Calendar.MINUTE));
        textViewAppointmentTime.setText(timeStr);
        textViewAppointmentData.setText(a.getClient().getName());
    }
}
