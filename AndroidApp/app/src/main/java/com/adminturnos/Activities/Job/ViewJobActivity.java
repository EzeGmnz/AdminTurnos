package com.adminturnos.Activities.Job;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;

public class ViewJobActivity extends AppCompatActivity {
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);

        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);

        job = (Job) intent.getExtras().getSerializable("job");
        updateUI();
    }

    private void updateUI() {
        setTitle("");
        TextView textviewBusinessName = findViewById(R.id.textviewBusinessName);
        TextView textviewAddress = findViewById(R.id.textviewAddress);

        textviewBusinessName.setText(job.getPlace().getBusinessName());
        textviewAddress.setText(job.getPlace().getAddress());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}