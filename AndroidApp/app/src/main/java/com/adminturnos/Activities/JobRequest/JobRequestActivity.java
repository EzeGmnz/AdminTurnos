package com.adminturnos.Activities.JobRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Builder.BuilderListJobRequest;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.ObjectInterfaces.JobRequest;
import com.adminturnos.ObjectViews.ViewJobRequest;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class JobRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ViewJobRequest> viewJobRequestList;
    private FlexibleAdapter<ViewJobRequest> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_request);

        this.recyclerView = findViewById(R.id.recyclerViewJobRequests);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initUI();
    }

    private void initUI() {

        findViewById(R.id.btnNewJobRequest).setOnClickListener(new ClickListenerNewJobRequest());

        setTitle("");
        getJobRequests();
    }

    private void getJobRequests() {
        this.viewJobRequestList = new ArrayList<>();
        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_JOB_REQUEST,
                null,
                new CallbackGetJobRequests()
        );

    }

    private void populateRecyclerView() {
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(layoutManager);
                adapter = new FlexibleAdapter<>(viewJobRequestList);
                recyclerView.setAdapter(adapter);
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();

        for (ViewJobRequest viewJobRequest : viewJobRequestList) {
            if (viewJobRequest.isAccepted()) {
                acceptJobRequest(viewJobRequest.getJobRequest());
            } else if (viewJobRequest.isDenied()) {
                cancelJobRequest(viewJobRequest.getJobRequest());
            }
        }
    }

    private void acceptJobRequest(JobRequest jobRequest) {
        Map<String, String> body = new HashMap<>();
        body.put("place_id", jobRequest.getPlace().getId());
        body.put("serviceprovider", jobRequest.getCustomUser().getId());

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_ACCEPT_JOB_REQUEST,
                body,
                new CallbackDecidedJobRequest()
        );
    }

    private void cancelJobRequest(JobRequest jobRequest) {
        Map<String, String> body = new HashMap<>();
        body.put("place_id", jobRequest.getPlace().getId());
        body.put("serviceprovider", jobRequest.getCustomUser().getId());

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_CANCEL_JOB_REQUEST,
                body,
                new CallbackDecidedJobRequest()
        );
    }

    private static class CallbackDecidedJobRequest extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // TODO
        }
    }

    private class CallbackGetJobRequests extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            try {

                List<JobRequest> aux = new BuilderListJobRequest().build(response);

                for (JobRequest jobRequest : aux) {
                    viewJobRequestList.add(new ViewJobRequest(jobRequest));
                }

                populateRecyclerView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClickListenerNewJobRequest implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), NewJobRequestActivity.class);
            startActivity(intent);
        }
    }
}