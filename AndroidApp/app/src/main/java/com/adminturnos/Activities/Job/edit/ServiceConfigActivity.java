package com.adminturnos.Activities.Job.edit;

import android.app.Activity;
import android.content.Intent;
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
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfigActivity extends AppCompatActivity {

    private Job job;
    private NonSwipeableViewPager viewPager;
    private ServiceConfigureFragment serviceConfigureFragment;
    private MenuItem removeServiceMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_config);

        this.job = ((Job) getIntent().getExtras().getSerializable("job")).clone();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(job.getPlace().getBusinessName());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        }, 100);
    }

    private void initUI() {
        this.viewPager = findViewById(R.id.view_pager);
        this.serviceConfigureFragment = new ServiceConfigureFragment(job, new ListenerConfirmConfig());

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ServiceSelectionFragment(job, new ListenerServiceClick()));
        fragments.add(serviceConfigureFragment);

        viewPager.setAdapter(new ServiceConfigPagerAdapter(getSupportFragmentManager(), fragments));
    }

    private void returnOK() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("job", job);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);

        finish();
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
            returnOK();
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
}