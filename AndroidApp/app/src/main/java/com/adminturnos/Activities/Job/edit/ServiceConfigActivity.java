package com.adminturnos.Activities.Job.edit;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.adminturnos.CustomViews.NonSwipeableViewPager;
import com.adminturnos.Database.JobRepositoryManagerRemote;
import com.adminturnos.Listeners.RepositoryGetJobListener;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfigActivity extends AppCompatActivity {

    private Job job;

    private NonSwipeableViewPager viewPager;
    private ServiceConfigureFragment serviceConfigureFragment;
    private ServiceSelectionFragment serviceSelectionFragment;

    private MenuItem removeServiceMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_config);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        final String jobId = getIntent().getExtras().getString("jobId");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JobRepositoryManagerRemote.getInstance().getJob(jobId, new ListenerGetJob());
            }
        }, 100);
    }

    private void initUI() {
        getSupportActionBar().setTitle(job.getPlace().getBusinessName());
        viewPager = findViewById(R.id.view_pager);

        serviceConfigureFragment = new ServiceConfigureFragment(job, new ListenerConfirmConfig());
        serviceSelectionFragment = new ServiceSelectionFragment(job, new ListenerServiceClick());

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(serviceSelectionFragment);
        fragments.add(serviceConfigureFragment);

        viewPager.setAdapter(new ServiceConfigPagerAdapter(getSupportFragmentManager(), fragments));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_config_service, menu);
        removeServiceMenuItem = menu.findItem(R.id.item_delete_service);
        removeServiceMenuItem.setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            finish();
        } else {
            showSelectService();
        }
    }

    private void showSelectService() {
        getSupportActionBar().setTitle(job.getPlace().getBusinessName());
        removeServiceMenuItem.setVisible(false);
        viewPager.setCurrentItem(0);
    }

    private void showConfigureService(Service service) {
        getSupportActionBar().setTitle(service.getName());
        serviceConfigureFragment.setServiceToConfigure(service);
        removeServiceMenuItem.setVisible(true);
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_delete_service:
                serviceConfigureFragment.removeService();
                break;
        }
        return true;
    }

    private static class ServiceConfigPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public ServiceConfigPagerAdapter(FragmentManager fm, List<Fragment> frags) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragmentList = frags;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private class ListenerServiceClick implements ServiceSelectionFragment.ServiceClickListener {
        @Override
        public void onServiceViewClick(Service service) {
            showConfigureService(service);
        }
    }

    private class ListenerConfirmConfig implements ServiceConfigureFragment.ListenerConfirm {

        @Override
        public void onConfirmServiceConfiguration() {

            showSelectService();
        }
    }

    private class ListenerGetJob implements RepositoryGetJobListener {
        @Override
        public void onFetch(Job j) {
            job = j;
            initUI();
        }
    }
}