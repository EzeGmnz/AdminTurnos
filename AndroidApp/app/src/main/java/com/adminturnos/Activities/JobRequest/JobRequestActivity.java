package com.adminturnos.Activities.JobRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Builder.BuilderJobRequest;
import com.adminturnos.Builder.ObjectBuilder;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.ObjectInterfaces.JobRequest;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class JobRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<JobRequest> jobRequestList;

    private AdapterRecyclerViewJobRequest adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_request);

        this.recyclerView = findViewById(R.id.recyclerViewJobRequests);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);

        updateUI();
    }

    private void updateUI() {
        setTitle("");
        getJobRequests();
    }

    private void getJobRequests() {
        this.jobRequestList = new ArrayList<>();
        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_JOB_REQUEST,
                null,
                new CallbackGetJobRequests()
        );

    }

    private void populateRecyclerView() {
        this.adapter = new AdapterRecyclerViewJobRequest(jobRequestList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(layoutManager);
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

    private void acceptJobRequest(JobRequest jobRequest) {
        removeJobRequestFromList(jobRequest);

        Map<String, String> body = new HashMap<>();
        body.put("place_id", jobRequest.getPlaceID());
        body.put("serviceprovider_from", jobRequest.getUserID());

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_ACCEPT_JOB_REQUEST,
                body,
                new CallbackDecidedJobRequest()
        );
    }

    private void cancelJobRequest(JobRequest jobRequest) {
        removeJobRequestFromList(jobRequest);

        Map<String, String> body = new HashMap<>();
        body.put("place_id", jobRequest.getPlaceID());
        body.put("serviceprovider_from", jobRequest.getUserID());

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_CANCEL_JOB_REQUEST,
                body,
                new CallbackDecidedJobRequest()
        );
    }

    private void removeJobRequestFromList(JobRequest jobRequest) {
        jobRequestList.remove(jobRequest);
        adapter.notifyDataSetChanged();
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

                ObjectBuilder<JobRequest> builder = new BuilderJobRequest();
                for (Iterator<String> it = response.keys(); it.hasNext(); ) {
                    JSONArray jsonArray = response.getJSONArray(it.next());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jobRequestList.add(builder.build(jsonArray.getJSONObject(i)));
                    }
                }

                populateRecyclerView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class AdapterRecyclerViewJobRequest extends RecyclerView.Adapter<AdapterRecyclerViewJobRequest.ViewHolderJobRequest> {
        private List<JobRequest> jobRequestList;

        public AdapterRecyclerViewJobRequest(List<JobRequest> myDataset) {
            jobRequestList = myDataset;
        }

        @Override
        public ViewHolderJobRequest onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.job_request_layout, parent, false);

            return new ViewHolderJobRequest(view);
        }

        @Override
        public void onBindViewHolder(ViewHolderJobRequest holder, final int position) {
            JobRequest request = jobRequestList.get(position);
            holder.textViewToPlace.setText(request.getPlace());
            holder.textViewFromWho.setText(request.getUserID());

            holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptJobRequest(jobRequestList.get(position));
                }
            });

            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelJobRequest(jobRequestList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return jobRequestList.size();
        }

        public class ViewHolderJobRequest extends RecyclerView.ViewHolder {
            public TextView textViewToPlace;
            public TextView textViewFromWho;
            public Button btnConfirm, btnCancel;

            public ViewHolderJobRequest(View view) {
                super(view);
                this.textViewToPlace = view.findViewById(R.id.textViewToPlace);
                this.textViewFromWho = view.findViewById(R.id.textViewFromWho);
                this.btnCancel = view.findViewById(R.id.btnCancel);
                this.btnConfirm = view.findViewById(R.id.btnConfirm);
            }
        }
    }

}