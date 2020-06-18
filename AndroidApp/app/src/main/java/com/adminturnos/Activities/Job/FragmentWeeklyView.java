package com.adminturnos.Activities.Job;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;


public class FragmentWeeklyView extends Fragment {

    public FragmentWeeklyView(Job job) {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_view, container, false);
    }
}