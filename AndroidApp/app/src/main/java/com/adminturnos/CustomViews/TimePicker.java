package com.adminturnos.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adminturnos.R;

import java.util.Calendar;

public class TimePicker {

    private final static int minuteStep = 30;
    private TextView labelTime;
    private Calendar calendar;

    public TimePicker(ViewGroup parent) {
        calendar = Calendar.getInstance();
        clear();
        inflateView(parent);
    }

    private void inflateView(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup timePickerView = (ViewGroup) inflater.inflate(R.layout.time_picker_layout, parent, false);

        this.labelTime = timePickerView.findViewById(R.id.labelTime);

        timePickerView.findViewById(R.id.btnRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseTime();
                updateUI();
            }
        });

        timePickerView.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime();
                updateUI();
            }
        });

        parent.addView(timePickerView);
    }

    private void addTime() {
        calendar.add(Calendar.MINUTE, minuteStep);
    }

    private void decreaseTime() {
        calendar.add(Calendar.MINUTE, -minuteStep);
    }

    private void updateUI() {
        String timeStr = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        labelTime.setText(timeStr);
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
    }
}
