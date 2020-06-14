package com.adminturnos.Activities.MainScreen;

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
import com.adminturnos.Activities.Place.ViewPlaceActivity;
import com.adminturnos.Builder.BuilderJob;
import com.adminturnos.Builder.BuilderPlace;
import com.adminturnos.Builder.ObjectBuilder;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.R;
import com.adminturnos.Values;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FragmentMain extends Fragment {
    private AdapterRecyclerViewJobs adapterJob;
    private AdapterRecyclerViewOwnedPlaces adapterOwnedPlaces;

    private List<Job> jobList;
    private List<Place> ownedPlacesList;

    private RecyclerView recyclerViewJob, recyclerViewOwnedPlaces;
    private RecyclerViewJobsListener recyclerViewJobsListener;
    private RecyclerViewOwnedPlacesListener recyclerViewOwnedPlacesListener;

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
        recyclerViewOwnedPlacesListener = new RecyclerViewOwnedPlacesListener();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOwnedPlaces.setLayoutManager(layoutManager);

        adapterOwnedPlaces = new AdapterRecyclerViewOwnedPlaces(ownedPlacesList);
        recyclerViewOwnedPlaces.setAdapter(adapterOwnedPlaces);
    }

    private void populateJobs() {
        jobList = new ArrayList<>();

        recyclerViewJob = getView().findViewById(R.id.recyclerViewJobs);
        recyclerViewJobsListener = new RecyclerViewJobsListener();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewJob.setLayoutManager(layoutManager);

        adapterJob = new AdapterRecyclerViewJobs(jobList);
        recyclerViewJob.setAdapter(adapterJob);
    }

    private class CallbackGetJobs implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray jobs = (JSONArray) jsonObject.get("jobs");

                ObjectBuilder<Job> builder = new BuilderJob();
                for (int i = 0; i < jobs.length(); i++) {
                    jobList.add(builder.build(jobs.getJSONObject(i)));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterJob.notifyDataSetChanged();
                    }
                });

            } catch (JSONException ignored) {

            }
        }
    }

    private class CallbackGetOwnedPlaces implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray places = (JSONArray) jsonObject.get("places");

                ObjectBuilder<Place> builder = new BuilderPlace();
                for (int i = 0; i < places.length(); i++) {
                    ownedPlacesList.add(builder.build(places.getJSONObject(i)));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterOwnedPlaces.notifyDataSetChanged();
                    }
                });
            } catch (JSONException ignored) {

            }
        }
    }

    public class AdapterRecyclerViewJobs extends RecyclerView.Adapter<AdapterRecyclerViewJobs.ViewHolderJob> {
        private List<Job> jobList;

        public AdapterRecyclerViewJobs(List<Job> jobList) {
            this.jobList = jobList;
        }

        @Override
        public ViewHolderJob onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_main_items, parent, false);

            v.setOnClickListener(recyclerViewJobsListener);
            return new ViewHolderJob(v);
        }

        @Override
        public void onBindViewHolder(ViewHolderJob holder, int position) {
            holder.textViewBusinessName.setText(jobList.get(position).getPlace().getBusinessName());
            holder.textViewAddress.setText(jobList.get(position).getPlace().getAddress());
        }

        @Override
        public int getItemCount() {
            return jobList.size();
        }

        public class ViewHolderJob extends RecyclerView.ViewHolder {
            public View relativeLayout;
            public TextView textViewBusinessName;
            public TextView textViewAddress;

            public ViewHolderJob(View v) {
                super(v);
                this.relativeLayout = v;
                this.textViewBusinessName = v.findViewById(R.id.textviewBusinessName);
                this.textViewAddress = v.findViewById(R.id.textviewAddress);
            }
        }
    }

    public class AdapterRecyclerViewOwnedPlaces extends RecyclerView.Adapter<AdapterRecyclerViewOwnedPlaces.ViewHolderOwnedPlaces> {
        private List<Place> ownedPlaceList;

        public AdapterRecyclerViewOwnedPlaces(List<Place> ownedPlaces) {
            this.ownedPlaceList = ownedPlaces;
        }

        @Override
        public ViewHolderOwnedPlaces onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_main_items, parent, false);

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

    private class RecyclerViewJobsListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = recyclerViewJob.getChildLayoutPosition(view);
            Job job = jobList.get(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("job", job);

            Intent intent = new Intent(getActivity(), ViewJobActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class RecyclerViewOwnedPlacesListener implements View.OnClickListener {

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