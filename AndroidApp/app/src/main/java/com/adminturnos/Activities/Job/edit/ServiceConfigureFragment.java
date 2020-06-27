package com.adminturnos.Activities.Job.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adminturnos.CustomViews.DayPicker;
import com.adminturnos.CustomViews.TimePicker;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.Objects.DayScheduleNormal;
import com.adminturnos.Objects.ProvidesNormal;
import com.adminturnos.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ServiceConfigureFragment extends Fragment {

    private Job job;
    private Service service;
    private TimePicker timePicker;
    private DayPicker dayPicker;
    private TextInputEditText inputPrice, inputParallelism;
    private TextInputLayout tilParallelism, tilPrice;
    private ListenerConfirm listenerBtnConfirm;

    public ServiceConfigureFragment(Job job, ListenerConfirm listenerBtnConfirm) {
        this.job = job;
        this.listenerBtnConfirm = listenerBtnConfirm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_service_configure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.timePicker = new TimePicker((ViewGroup) view.findViewById(R.id.timePicker));
        this.dayPicker = new DayPicker((ViewGroup) view.findViewById(R.id.dayPicker));
        this.inputParallelism = view.findViewById(R.id.inputParallelism);
        this.inputPrice = view.findViewById(R.id.inputPrice);
        this.tilParallelism = view.findViewById(R.id.tilParallelism);
        this.tilPrice = view.findViewById(R.id.tilPrice);

        view.findViewById(R.id.btnConfirm).setOnClickListener(new ListenerBtnConfirm());
    }

    private void refreshUI() {
        Provides p = null;
        dayPicker.clearSelection();
        for (DaySchedule ds : job.getDaySchedules()) {
            p = ds.getProvidesForService(service.getId());
            if (p != null) {
                dayPicker.selectDay(ds.getDayOfWeek());
            }
        }

        if (p != null) {
            timePicker.setTime(p.getDuration());
            inputPrice.setText(p.getPrice() + "");
            inputParallelism.setText(p.getParallelism() + "");
        } else {
            timePicker.clear();
            inputPrice.setText("");
            inputParallelism.setText("");
        }

    }

    public void setServiceToConfigure(Service service) {
        this.service = service;
        refreshUI();
    }

    private void configureJob() {
        if (validateData()) {
            createOrUpdateProvides();
        }
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(inputPrice.getText())) {

            tilPrice.setError("Este campo es obligatorio");
            return false;
        } else {
            tilPrice.setError(null);
        }

        if (TextUtils.isEmpty(inputParallelism.getText())) {

            tilParallelism.setError("Este campo es obligatorio");
            return false;
        } else {
            tilParallelism.setError(null);
        }

        if (dayPicker.getSelectedDays().size() == 0) {
            //TODO show error
            return false;
        }

        //TODO show error
        return timePicker.getTime().get(Calendar.MINUTE) != 0 ||
                timePicker.getTime().get(Calendar.HOUR_OF_DAY) != 0;
    }

    public void removeService() {
        dayPicker.clearSelection();
        finishConfiguration();
    }

    private void createOrUpdateProvides() {
        for (Integer day : dayPicker.getSelectedDays()) {

            DaySchedule ds = job.getDaySchedule(day);
            if (ds == null) {
                ds = createDaySchedule(day);
                job.addDaySchedule(ds);
            }

            Provides p = ds.getProvidesForService(service.getId());
            if (p == null) {
                p = createProvides();
                ds.addProvides(p);
            } else {
                updateProvides(p);
            }
        }

        finishConfiguration();
    }

    private DaySchedule createDaySchedule(int day) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.set(Calendar.HOUR_OF_DAY, 8);
        start.set(Calendar.MINUTE, 0);
        end.set(Calendar.HOUR_OF_DAY, 16);
        end.set(Calendar.MINUTE, 0);

        return new DayScheduleNormal(
                null, day, start, end, null, null, null
        );
    }

    private void updateProvides(Provides p) {
        p.setPrice(Float.parseFloat(inputPrice.getText().toString()));
        p.setDuration(timePicker.getTime());
        p.setParallelism(Integer.parseInt(inputParallelism.getText().toString()));
    }

    private Provides createProvides() {
        return new ProvidesNormal(
                null,
                service,
                Float.parseFloat(inputPrice.getText().toString()),
                timePicker.getTime(),
                Integer.parseInt(inputParallelism.getText().toString())
        );
    }

    private void finishConfiguration() {
        removeUnselectedDays();
        removeEmptyDaySchedules();
        listenerBtnConfirm.onConfirmServiceConfiguration();
    }

    private void removeEmptyDaySchedules() {
        List<DaySchedule> toRemove = new ArrayList<>();
        for (DaySchedule ds : job.getDaySchedules()) {
            if (ds.getProvides().isEmpty()) {
                toRemove.add(ds);
            }
        }

        for (DaySchedule ds : toRemove) {
            job.getDaySchedules().remove(ds);
        }
    }

    private void removeUnselectedDays() {
        for (DaySchedule ds : job.getDaySchedules()) {
            Provides p = ds.getProvidesForService(service.getId());
            if (p != null && !dayPicker.getSelectedDays().contains(ds.getDayOfWeek())) {
                ds.removeProvides(p);
            }
        }
    }

    public interface ListenerConfirm {
        void onConfirmServiceConfiguration();
    }

    private class ListenerBtnConfirm implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            configureJob();
        }
    }
}