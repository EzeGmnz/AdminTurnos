package com.adminturnos.Activities.Job;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.adminturnos.Activities.Job.edit.EditJobActivity;
import com.adminturnos.Database.JobRepositoryManagerRemote;
import com.adminturnos.Listeners.RepositoryGetJobListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String jobId = getIntent().getExtras().getString("jobId");
        JobRepositoryManagerRemote.getInstance().getJob(jobId, new ListenerGetJob());
    }

    private void initUI() {
        getSupportActionBar().setTitle(job.getPlace().getBusinessName());
        viewPager = findViewById(R.id.viewPagerJob);
        tabLayout = findViewById(R.id.tabLayoutJob);

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViewPager();
            }
        }, 100);
    }

    private void initViewPager() {

        AppointmentViewsAdapter adapter = new AppointmentViewsAdapter(getSupportFragmentManager());

        fragmentDailyView = new FragmentDailyView(job);
        fragmentMonthlyView = new FragmentMonthlyView(job, new ListenerOnDayClicked());

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
    public void onBackPressed() {
        finish();
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

    private void startEditJobActivity() {
        Intent intent = new Intent(this, EditJobActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("jobId", job.getId());

        intent.putExtras(bundle);
        startActivityForResult(intent, Values.RC_EDIT_JOB);
    }

    private static class AppointmentViewsAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;
        private List<String> titleList;

        public AppointmentViewsAdapter(@NonNull FragmentManager fm) {
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

    private class ListenerGetJob implements RepositoryGetJobListener {
        @Override
        public void onFetch(Job j) {
            job = j;
            initUI();
        }
    }
}