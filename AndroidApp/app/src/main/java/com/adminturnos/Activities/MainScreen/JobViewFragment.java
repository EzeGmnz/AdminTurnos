package com.adminturnos.Activities.MainScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adminturnos.Activities.Job.ViewJobActivity;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;


public class JobViewFragment extends Fragment {

    private Job job;

    public JobViewFragment(Job job) {
        this.job = job;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_job_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    private void initUI() {
        getView().findViewById(R.id.cardViewContainer).setOnClickListener(new OnJobClickListener());
        ((TextView) getView().findViewById(R.id.labelBusinessName)).setText(job.getPlace().getBusinessName());
    }

    private class OnJobClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("jobId", job.getId());

            Intent intent = new Intent(getActivity(), ViewJobActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}