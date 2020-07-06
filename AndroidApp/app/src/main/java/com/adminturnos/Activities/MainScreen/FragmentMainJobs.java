package com.adminturnos.Activities.MainScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.adminturnos.Activities.JobRequest.NewJobRequestActivity;
import com.adminturnos.Database.JobRepositoryManagerRemote;
import com.adminturnos.Listeners.RepositoryGetJobListListener;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentMainJobs extends Fragment {
    private List<Job> jobList;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = getView().findViewById(R.id.recyclerViewJobs);
        getJobs();
    }

    private void getJobs() {
        JobRepositoryManagerRemote.getInstance().getJobs(new GetJobsListener());
    }

    private void populateJobs() {
        if (hasJobs()) {
            initViewPager();
            displayHasJobsUI();
        } else {
            displayNoJobs();
        }
    }

    private boolean hasJobs() {
        return jobList.size() > 0;
    }

    private void initViewPager() {
        jobList.sort(new JobTodayComparator());
        JobFragmentPagerAdapter adapterJob = new JobFragmentPagerAdapter(getChildFragmentManager());

        for (Job j : jobList) {
            adapterJob.addFragment(new JobViewFragment(j));
        }

        viewPager.setAdapter(adapterJob);
    }

    private void displayHasJobsUI() {
        ((TabLayout) getView().findViewById(R.id.tabLayout)).setupWithViewPager(viewPager);

        viewPager.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.noJobsView).setVisibility(View.GONE);
    }

    private void displayNoJobs() {
        viewPager.setVisibility(View.GONE);
        getView().findViewById(R.id.noJobsView).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.noJobsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewJobRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    public void refresh() {
        JobRepositoryManagerRemote.getInstance().invalidate();
        getJobs();
    }

    private static class JobFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private List<JobViewFragment> fragmentList;

        public JobFragmentPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragmentList = new ArrayList<>();
        }

        public void addFragment(JobViewFragment jobViewFragment) {
            fragmentList.add(jobViewFragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private static class JobTodayComparator implements java.util.Comparator<Job> {

        @Override
        public int compare(Job o1, Job o2) {
            Calendar calendar = Calendar.getInstance();
            if (o2.getDaySchedule(calendar.get(Calendar.DAY_OF_WEEK)) == null) {
                return -1;
            }
            if (o1.getDaySchedule(calendar.get(Calendar.DAY_OF_WEEK)) == null) {
                return 1;
            }

            return 0;
        }
    }

    private class GetJobsListener implements RepositoryGetJobListListener {

        @Override
        public void onFetch(List<Job> list) {
            jobList = list;
            populateJobs();
        }
    }
}