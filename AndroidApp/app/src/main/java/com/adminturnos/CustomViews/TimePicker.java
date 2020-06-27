package com.adminturnos.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adminturnos.R;

import java.util.Calendar;

public class TimePicker {

    private final static int MINUTE_STEP = 30;
    private static final int MAX_HOURS = 3;
    private TextView labelHour, labelMinute;
    private Calendar calendar;
    private ImageButton btnAdd, btnRemove;

    public TimePicker(ViewGroup parent) {
        calendar = Calendar.getInstance();
        inflateView(parent);
        clear();
    }

    private void inflateView(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup timePickerView = (ViewGroup) inflater.inflate(R.layout.time_picker_layout, parent, false);

        labelHour = timePickerView.findViewById(R.id.labelHour);
        labelMinute = timePickerView.findViewById(R.id.labelMinute);

        btnRemove = timePickerView.findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseTime();
                updateUI();
            }
        });

        btnAdd = timePickerView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime();
                updateUI();
            }
        });

        parent.addView(timePickerView);
    }

    private void addTime() {
        calendar.add(Calendar.MINUTE, MINUTE_STEP);
    }

    private void decreaseTime() {
        calendar.add(Calendar.MINUTE, -MINUTE_STEP);
    }

    private void updateUI() {
        String hrString = calendar.get(Calendar.HOUR_OF_DAY) + "h";
        String minuteString = calendar.get(Calendar.MINUTE) + "m";
        labelHour.setText(hrString);
        labelMinute.setText(minuteString);

        if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
            btnRemove.setVisibility(View.INVISIBLE);
        } else {
            btnRemove.setVisibility(View.VISIBLE);
        }

        if (calendar.get(Calendar.HOUR_OF_DAY) == MAX_HOURS) {
            btnAdd.setVisibility(View.INVISIBLE);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
        }
    }

    public Calendar getTime() {
        return calendar;
    }

    public void setTime(Calendar calendar) {
        this.calendar = calendar;
        updateUI();
    }

    public void clear() {
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        updateUI();
    }
}
