package com.adminturnos.Activities.Job.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Activities.ObjectConfiguratorCoordinator;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class EditJobActivity extends AppCompatActivity {

    private Job job;
    private ObjectConfiguratorCoordinator coordinator;
    private List<ObjectConfigurator> fragments;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        this.job = (Job) getIntent().getExtras().getSerializable("job");
        initUI();
    }

    private void initUI() {
        getSupportActionBar().setTitle(job.getPlace().getBusinessName());
        if (hasDayShedules()) {
            displayMainConfiguration();
        } else {
            displayTemplateSelection();
        }
    }

    private void displayMainConfiguration() {
        fragments = new ArrayList<>();
        fragments.add(new MainConfigureJobFragment(job));
        initCoordinator();
    }

    private void displayTemplateSelection() {
        fragments = new ArrayList<>();
        SelectTemplateFragment selectTemplateFragment = new SelectTemplateFragment(new BtnConfirmClickListener());
        fragments.add(selectTemplateFragment);
        fragments.add(new MainConfigureJobFragment(null));
        initCoordinator();
    }

    private void initCoordinator() {
        coordinator = new ObjectConfiguratorCoordinator(
                getSupportFragmentManager(),
                findViewById(android.R.id.content).getRootView(),
                fragments,
                new ListenerCoordinatorMain()
        );
        Bundle extras = new Bundle();
        extras.putSerializable("job", job);
        coordinator.setInitialExtras(extras);
    }

    private boolean hasDayShedules() {
        return job.getDaySchedules().size() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_job, menu);
        this.menu = menu;

        // May want to look for a better way of changing item text color
        MenuItem item = menu.findItem(R.id.save_job);
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getColor(R.color.black)), 0, spanString.length(), 0); //fix the color to white
        item.setTitle(spanString);

        toggleSaveMenuItem();
        return true;
    }

    @Override
    public void onBackPressed() {
        toggleSaveMenuItem();
        coordinator.prev();
    }

    private void toggleSaveMenuItem() {
        MenuItem saveItem = menu.findItem(R.id.save_job);
        saveItem.setVisible(!saveItem.isVisible());
    }

    private void returnCancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_job:
                saveJob();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveJob() {
        coordinator.next();
    }

    private void saveDaySchedules(Bundle bundle) {
        job = (Job) bundle.getSerializable("job");

        JSONObject body = new JSONObject();
        try {

            body.put("job_id", job.getId());

            JSONObject days = new JSONObject();
            for (DaySchedule d : job.getDaySchedules()) {
                String dayStart = d.getDayStart().get(Calendar.HOUR_OF_DAY) + ":" + d.getDayStart().get(Calendar.MINUTE) + ":00";
                String dayEnd = d.getDayEnd().get(Calendar.HOUR_OF_DAY) + ":" + d.getDayEnd().get(Calendar.MINUTE) + ":00";

                String pauseEnd = null, pauseStart = null;
                if (d.getPauseStart() != null) {
                    pauseStart = d.getPauseStart().get(Calendar.HOUR_OF_DAY) + ":" + d.getPauseStart().get(Calendar.MINUTE) + ":00";
                    pauseEnd = d.getPauseEnd().get(Calendar.HOUR_OF_DAY) + ":" + d.getPauseEnd().get(Calendar.MINUTE) + ":00";
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("day_start", dayStart);
                jsonObject.put("day_end", dayEnd);
                jsonObject.put("pause_start", pauseStart);
                jsonObject.put("pause_end", pauseEnd);
                days.put(d.getDayOfWeek() + "", jsonObject);
            }
            body.put("days", days);

            DatabaseDjangoWrite.getInstance().POSTJSON(
                    this,
                    Values.DJANGO_URL_NEW_DAY_SCHEDULE,
                    body,
                    new CallbackSaveDaySchedule()
            );


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveProvides() {
        returnOK(); //TODO
    }

    private class ListenerCoordinatorMain implements ObjectConfiguratorCoordinator.ListenerCoordinator {
        @Override
        public void onFinish(Bundle bundle) {
            saveDaySchedules(bundle);
        }

        @Override
        public void onCanceled() {
            returnCancel();
        }
    }

    public class BtnConfirmClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            coordinator.next();
            toggleSaveMenuItem();
        }
    }

    private class CallbackSaveDaySchedule extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            saveProvides();
        }
    }

}