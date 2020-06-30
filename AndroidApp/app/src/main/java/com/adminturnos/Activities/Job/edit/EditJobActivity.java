package com.adminturnos.Activities.Job.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adminturnos.Database.JobRepositoryManagerRemote;
import com.adminturnos.Listeners.RepositoryGetJobListener;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;
import com.adminturnos.Values;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditJobActivity extends AppCompatActivity {

    private Job originalJob;
    private Job editedJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");
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
        getSupportActionBar().setTitle(originalJob.getPlace().getBusinessName());
        findViewById(R.id.btnServiceConfiguration).setOnClickListener(new ListenerBtnServiceConfiguration());
        DayScheduleConfigFragment dayScheduleConfigFragment = new DayScheduleConfigFragment(editedJob);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, dayScheduleConfigFragment);
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
        JobRepositoryManagerRemote.getInstance().saveJob(originalJob, null);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (hasJobChanged()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showSaveDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("¿Guardar cambios?")
                .setPositiveButton("Guardar", new ListenerSaveChanges())
                .setNeutralButton("Descartar", new ListenerDiscardChanges())
                .show();
    }

    private boolean hasJobChanged() {
        return !editedJob.equals(originalJob);
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
        JobRepositoryManagerRemote.getInstance().saveJobToStorage(editedJob.getId());
        finish();
    }

    private class ListenerSaveChanges implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            saveJob();
        }
    }

    private class ListenerDiscardChanges implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            returnCancel();
        }
    }

    private class ListenerBtnServiceConfiguration implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("jobId", editedJob.getId());

            Intent intent = new Intent(getApplicationContext(), ServiceConfigActivity.class);
            intent.putExtras(bundle);

            startActivityForResult(intent, Values.RC_EDIT_SERVICES);
        }
    }

    private class ListenerGetJob implements RepositoryGetJobListener {
        @Override
        public void onFetch(Job job) {
            originalJob = job.clone();
            editedJob = job;
            initUI();
        }
    }
}