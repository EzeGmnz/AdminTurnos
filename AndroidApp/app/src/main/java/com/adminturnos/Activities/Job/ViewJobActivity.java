package com.adminturnos.Activities.Job;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.adminturnos.Activities.Job.edit.EditJobActivity;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;
import com.adminturnos.Values;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewJobActivity extends AppCompatActivity {
    private Job job;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentDailyView fragmentDailyView;
    private FragmentMonthlyView fragmentMonthlyView;

    private boolean edited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);

        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        job = (Job) intent.getExtras().getSerializable("job");
        edited = false;
        updateUI();
    }

    private void updateUI() {

        if (hasDaySchedules()) {
            this.viewPager = findViewById(R.id.viewPagerJob);
            this.tabLayout = findViewById(R.id.tabLayoutJob);

            getSupportActionBar().setTitle(job.getPlace().getBusinessName());

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    populateContainer();
                }
            }, 100);
        } else {
            startEditJobActivity();
        }
    }

    private boolean hasDaySchedules() {
        return job.getDaySchedules().size() > 0;
    }

    private void populateContainer() {
        AdapterTab adapter = new AdapterTab(getSupportFragmentManager());

        this.fragmentDailyView = new FragmentDailyView(job);
        this.fragmentMonthlyView = new FragmentMonthlyView(job, new ListenerOnDayClicked());

        adapter.addFragment(fragmentDailyView, "Diario");
        adapter.addFragment(fragmentMonthlyView, "Mensual");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_job:
                startEditJobActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (edited) {
            setResult(RESULT_OK);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void startEditJobActivity() {
        Intent intent = new Intent(this, EditJobActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("job", job);

        intent.putExtras(bundle);
        startActivityForResult(intent, Values.RC_EDIT_JOB);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Values.RC_EDIT_JOB) {
            if (resultCode == Activity.RESULT_OK) {
                this.job = (Job) data.getExtras().getSerializable("job");
                edited = true;
                updateUI();
            } else {
                if (!hasDaySchedules()) {
                    finish();
                }
            }
        }
    }

    private class AdapterTab extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;
        private List<String> titleList;

        public AdapterTab(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragmentList = new ArrayList<>();
            this.titleList = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.titleList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return this.titleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private class ListenerOnDayClicked implements FragmentMonthlyView.ListenerChangeDay {

        @Override
        public void onMonthDayClicked(Calendar day) {
            fragmentDailyView.setDay(day);
            viewPager.setCurrentItem(0);
        }
    }
}