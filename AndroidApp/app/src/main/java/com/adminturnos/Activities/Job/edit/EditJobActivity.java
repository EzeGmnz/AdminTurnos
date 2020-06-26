package com.adminturnos.Activities.Job.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class EditJobActivity extends AppCompatActivity {

    private Job originalJob;
    private Job editedJob;
    private MainConfigureJobFragment mainConfigureJobFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        this.originalJob = (Job) getIntent().getExtras().getSerializable("job");
        getSupportActionBar().setTitle(originalJob.getPlace().getBusinessName());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        }, 100);
    }

    private void initUI() {
        mainConfigureJobFragment = new MainConfigureJobFragment(originalJob);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mainConfigureJobFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_job, menu);

        // May want to look for a better way of changing item text color
        MenuItem item = menu.findItem(R.id.save_job);
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getColor(R.color.black)), 0, spanString.length(), 0);
        item.setTitle(spanString);

        return true;
    }

    private void returnCancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void returnOK() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("job", originalJob);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Values.RC_EDIT_SERVICES) {
            if (resultCode == RESULT_OK) {
                editedJob = (Job) data.getSerializableExtra("job");
                mainConfigureJobFragment.setJob(editedJob);
            }
        }
    }

    private void saveJob() {
        editedJob = mainConfigureJobFragment.getJob();
        saveDaySchedules();
    }

    private void saveDaySchedules() {
        JSONObject body = new JSONObject();

        try {
            body.put("job_id", editedJob.getId());

            JSONObject days = new JSONObject();
            for (DaySchedule d : editedJob.getDaySchedules()) {
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
        try {
            for (DaySchedule ds : editedJob.getDaySchedules()) {
                JSONObject body = new JSONObject();
                body.put("job_id", originalJob.getId());

                body.put("day_of_week", ds.getDayOfWeek());
                JSONArray servicesArray = new JSONArray();
                JSONObject costsJson = new JSONObject();
                JSONObject durationsJson = new JSONObject();
                JSONObject parallelismsJson = new JSONObject();

                for (Provides p : ds.getProvides()) {
                    String duration = p.getDuration().get(Calendar.HOUR_OF_DAY) + ":" + p.getDuration().get(Calendar.MINUTE) + ":00";
                    servicesArray.put(p.getService().getId());
                    costsJson.put(p.getService().getId(), p.getPrice());
                    durationsJson.put(p.getService().getId(), duration);
                    parallelismsJson.put(p.getService().getId(), p.getParallelism());
                }

                body.put("services", servicesArray);
                body.put("costs", costsJson);
                body.put("durations", durationsJson);
                body.put("parallelisms", parallelismsJson);

                DatabaseDjangoWrite.getInstance().POSTJSON(
                        this,
                        Values.DJANGO_URL_NEW_PROVIDES,
                        body,
                        new DatabaseCallback() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // TODO
                            }
                        }
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CallbackSaveDaySchedule extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            saveProvides();
            returnOK();
        }
    }

}