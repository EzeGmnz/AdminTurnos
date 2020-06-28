package com.adminturnos.Activities.MainScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Activities.Job.ViewJobActivity;
import com.adminturnos.Activities.JobRequest.NewJobRequestActivity;
import com.adminturnos.Activities.Place.ViewPlaceActivity;
import com.adminturnos.Builder.BuilderListJob;
import com.adminturnos.Builder.BuilderListPlace;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Functionality.JobAppointmentHolderWrapper;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class FragmentMain extends Fragment {
    private AdapterRecyclerViewJobs adapterJob;
    private AdapterRecyclerViewOwnedPlaces adapterOwnedPlaces;

    private List<Job> jobList;
    private List<Place> ownedPlacesList;

    private RecyclerView recyclerViewJob, recyclerViewOwnedPlaces;
    private OnJobClickListener onJobClickListener;
    private OnPlaceClickListener recyclerViewOwnedPlacesListener;

    public FragmentMain() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getOwnedPlaces();
        getJobs();
    }

    private void getOwnedPlaces() {
        populateOwnedPlaces();
        DatabaseDjangoRead.getInstance().GET(Values.DJANGO_URL_GET_OWNED_PLACES, null, new CallbackGetOwnedPlaces());
    }

    private void getJobs() {
        populateJobs();
        DatabaseDjangoRead.getInstance().GET(Values.DJANGO_URL_GET_JOBS, null, new CallbackGetJobs());
    }

    private void populateOwnedPlaces() {
        ownedPlacesList = new ArrayList<>();

        recyclerViewOwnedPlaces = getView().findViewById(R.id.recyclerViewOwnedPlaces);
        recyclerViewOwnedPlacesListener = new OnPlaceClickListener();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOwnedPlaces.setLayoutManager(layoutManager);

        adapterOwnedPlaces = new AdapterRecyclerViewOwnedPlaces(ownedPlacesList);
        recyclerViewOwnedPlaces.setAdapter(adapterOwnedPlaces);
    }

    private void populateJobs() {
        jobList = new ArrayList<>();

        recyclerViewJob = getView().findViewById(R.id.recyclerViewJobs);
        onJobClickListener = new OnJobClickListener();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewJob.setLayoutManager(layoutManager);

        adapterJob = new AdapterRecyclerViewJobs(jobList);
        recyclerViewJob.setAdapter(adapterJob);
    }

    private void fetchAppointmentsInBackground() {
        for (Job j : jobList) {
            JobAppointmentHolderWrapper.getInstance().getAppointmentManager(j.getId(), null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Values.RC_EDIT_JOB && resultCode == Activity.RESULT_OK) {
            getJobs();
        }
    }

    private void populateJobs(JSONObject response) {
        try {

            jobList.addAll(new BuilderListJob().build(response));
            if (jobList.size() > 0) {
                displayHasJobs();
                adapterJob.notifyDataSetChanged();
                fetchAppointmentsInBackground();
            } else {
                displayNoJobs();
            }

        } catch (JSONException ignored) {

        }
    }

    private void populatePlaces(JSONObject response) {
        try {
            ownedPlacesList.addAll(new BuilderListPlace().build(response));

            if (ownedPlacesList.size() > 0) {
                displayHasPlaces();
                adapterOwnedPlaces.notifyDataSetChanged();
            } else {
                displayNoPlaces();
            }

        } catch (JSONException ignored) {

        }
    }

    private void displayHasJobs() {
        recyclerViewJob.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.cardviewNoJobs).setVisibility(View.GONE);
    }

    private void displayHasPlaces() {
        recyclerViewOwnedPlaces.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.labelOwnedPlaces).setVisibility(View.VISIBLE);
    }

    private void displayNoJobs() {
        recyclerViewJob.setVisibility(View.GONE);
        getView().findViewById(R.id.cardviewNoJobs).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.cardviewNoJobs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewJobRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayNoPlaces() {
        recyclerViewOwnedPlaces.setVisibility(View.GONE);
        getView().findViewById(R.id.labelOwnedPlaces).setVisibility(View.GONE);
    }

    private class CallbackGetJobs extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            populateJobs(response);
        }
    }

    private class CallbackGetOwnedPlaces extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            populatePlaces(response);
        }
    }

    private class AdapterRecyclerViewJobs extends RecyclerView.Adapter<AdapterRecyclerViewJobs.ViewHolderJob> {
        private List<Job> jobList;

        public AdapterRecyclerViewJobs(List<Job> jobList) {
            this.jobList = jobList;
        }

        @Override
        public ViewHolderJob onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_job_item_layout, parent, false);

            v.setOnClickListener(onJobClickListener);
            return new ViewHolderJob(v);
        }

        @Override
        public void onBindViewHolder(ViewHolderJob holder, int position) {
            holder.textViewBusinessName.setText(jobList.get(position).getPlace().getBusinessName());
        }

        @Override
        public int getItemCount() {
            return jobList.size();
        }

        public class ViewHolderJob extends RecyclerView.ViewHolder {
            public View relativeLayout;
            public TextView textViewBusinessName;

            public ViewHolderJob(View v) {
                super(v);
                this.relativeLayout = v;
                this.textViewBusinessName = v.findViewById(R.id.textviewBusinessName);
            }
        }
    }

    private class AdapterRecyclerViewOwnedPlaces extends RecyclerView.Adapter<AdapterRecyclerViewOwnedPlaces.ViewHolderOwnedPlaces> {
        private List<Place> ownedPlaceList;

        public AdapterRecyclerViewOwnedPlaces(List<Place> ownedPlaces) {
            this.ownedPlaceList = ownedPlaces;
        }

        @Override
        public ViewHolderOwnedPlaces onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_place_item_layout, parent, false);

            v.setOnClickListener(recyclerViewOwnedPlacesListener);
            return new ViewHolderOwnedPlaces(v);
        }

        @Override
        public void onBindViewHolder(ViewHolderOwnedPlaces holder, int position) {
            holder.textViewBusinessName.setText(ownedPlaceList.get(position).getBusinessName());
            holder.textViewAddress.setText(ownedPlaceList.get(position).getAddress());
        }

        @Override
        public int getItemCount() {
            return ownedPlaceList.size();
        }

        public class ViewHolderOwnedPlaces extends RecyclerView.ViewHolder {
            public View relativeLayout;
            public TextView textViewBusinessName;
            public TextView textViewAddress;

            public ViewHolderOwnedPlaces(View v) {
                super(v);
                this.relativeLayout = v;
                this.textViewBusinessName = v.findViewById(R.id.textviewBusinessName);
                this.textViewAddress = v.findViewById(R.id.textviewAddress);
            }
        }
    }

    private class OnJobClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = recyclerViewJob.getChildLayoutPosition(view);
            Job job = jobList.get(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("job", job);

            Intent intent = new Intent(getActivity(), ViewJobActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, Values.RC_EDIT_JOB);
        }
    }

    private class OnPlaceClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = recyclerViewOwnedPlaces.getChildLayoutPosition(view);
            Place place = ownedPlacesList.get(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("place", place);

            Intent intent = new Intent(getActivity(), ViewPlaceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}