package com.adminturnos.Activities.Job;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adminturnos.CustomViews.CalendarDay;
import com.adminturnos.CustomViews.CalendarDayMonthly;
import com.adminturnos.Functionality.AppointmentManager;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMonthlyView extends Fragment {

    private final static int MAX_DISPLAYED_WEEKS = 8;
    private LinearLayout weekRowContainer;
    private AppointmentManager appointmentManager;
    private Calendar todayCalendar;
    private Job job;
    private List<CalendarDay> calendarDaysList;
    private Map<ViewGroup, Calendar> mapViewCalendar;
    private ListenerChangeDay listenerChangeDay;

    public FragmentMonthlyView(Job job, ListenerChangeDay listener) {

        this.job = job;
        this.listenerChangeDay = listener;
        todayCalendar = Calendar.getInstance();
        calendarDaysList = new ArrayList<>();
        mapViewCalendar = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_monthly_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weekRowContainer = view.findViewById(R.id.weekRowContainer);
        appointmentManager = job.getAppointmentManager();

        populateWeekRows();
    }

    private void populateWeekRows() {
        ViewGroup currentWeekRow = newWeekRow();
        addPastDays(currentWeekRow);

        final Calendar currentCalendar = Calendar.getInstance();
        CalendarDay currentCalendarDay;
        for (int i = 0; i < MAX_DISPLAYED_WEEKS; i++) {
            if (i > 0) {
                currentWeekRow = newWeekRow();
            }

            int j = 0;
            while (currentWeekRow.getChildCount() < 7) {

                currentCalendarDay = new CalendarDayMonthly(currentCalendar);
                currentCalendarDay.setAppointmentList(appointmentManager.getAppointmentsInDate(currentCalendar));

                ViewGroup dayView = newDayView(currentWeekRow);
                currentCalendarDay.populateView(dayView, getLayoutInflater());
                calendarDaysList.add(currentCalendarDay);

                mapViewCalendar.put(dayView, (Calendar) currentCalendar.clone());
                dayView.setOnClickListener(new ListenerDayClick());

                if (j == 0) {
                    dayView.setBackground(getResources().getDrawable(R.drawable.monthly_border_rectangle_leftmost));
                }
                j++;

                currentCalendar.add(Calendar.DATE, 1);

            }
        }

    }

    private void addPastDays(ViewGroup weekRow) {
        int monthDayStart = todayCalendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i < monthDayStart; i++) {
            LinearLayout invalidDayView = (LinearLayout) getLayoutInflater().inflate(R.layout.monthly_invalid_day, weekRow, false);
            weekRow.addView(invalidDayView);
        }
    }

    private ViewGroup newDayView(ViewGroup weekRow) {
        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.monthly_day_layout, weekRow, false);

        weekRow.addView(relativeLayout);
        return relativeLayout;
    }

    private ViewGroup newWeekRow() {
        LinearLayout weekRowView = (LinearLayout) getLayoutInflater().inflate(R.layout.monthly_weekrow_layout, weekRowContainer, false);
        weekRowContainer.addView(weekRowView);
        return weekRowView;
    }

    public interface ListenerChangeDay {
        void onMonthDayClicked(Calendar day);
    }

    private class ListenerDayClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Calendar calendar = mapViewCalendar.get(v);
            listenerChangeDay.onMonthDayClicked(calendar);
        }

    }
}