package com.adminturnos.Activities.MainScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
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
    private JobFragmentPagerAdapter adapterJob;
    private List<JobViewFragment> jobViewFragmentList;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jobViewFragmentList = new ArrayList<>();
        viewPager = getView().findViewById(R.id.recyclerViewJobs);
        getJobs();
    }

    private void getJobs() {
        JobRepositoryManagerRemote.getInstance().getJobs(new GetJobsListener());
    }

    private void populateJobs(List<Job> jobList) {
        jobViewFragmentList.clear();
        if (jobList.size() > 0) {

            jobList.sort(new JobTodayComparator());
            for (Job j : jobList) {
                jobViewFragmentList.add(new JobViewFragment(j));
            }
            displayHasJobs();

        } else {
            displayNoJobs();
        }
    }

    private void displayHasJobs() {
        adapterJob = new JobFragmentPagerAdapter(getChildFragmentManager(), jobViewFragmentList);
        viewPager.setAdapter(adapterJob);
        adapterJob.notifyDataSetChanged();

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

    private static class JobFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<JobViewFragment> fragmentList;

        public JobFragmentPagerAdapter(FragmentManager fm, List<JobViewFragment> frags) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragmentList = frags;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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
        public void onFetch(List<Job> jobList) {
            populateJobs(jobList);
        }
    }
}