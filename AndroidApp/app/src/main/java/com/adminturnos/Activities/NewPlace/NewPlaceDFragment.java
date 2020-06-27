package com.adminturnos.Activities.NewPlace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Builder.BuilderListJobType;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.ObjectInterfaces.JobType;
import com.adminturnos.ObjectViews.ViewJobTypeSelection;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class NewPlaceDFragment extends ObjectConfigurator {

    private List<ViewJobTypeSelection> jobTypeList;

    private boolean isPopulated;
    private RecyclerView recyclerView;
    private FlexibleAdapter<ViewJobTypeSelection> adapter;
    private SearchView searchView;

    public NewPlaceDFragment() {
        getJobTypes();
        isPopulated = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_place_d, container, false);
    }

    private void getJobTypes() {
        this.jobTypeList = new ArrayList<>();
        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_JOBTYPES,
                null,
                new CallbackGetJobTypes()
        );
    }

    private void populateRecyclerView() {
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new FlexibleAdapter<>(jobTypeList);
                recyclerView.setAdapter(adapter);
            }
        });
        initSearchView();
    }

    private void initSearchView() {
        this.searchView.setOnQueryTextListener(new ListenerQueryTextJobType());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.recyclerViewJobTypes);
        this.searchView = view.findViewById(R.id.searchViewJobTypeSelection);


        if (jobTypeList.size() > 0) {
            populateRecyclerView();
            this.isPopulated = true;
        }
    }

    @Override
    public void setExtras(Bundle bundle) {

    }

    @Override
    public Bundle getData() {
        Bundle bundle = new Bundle();

        ArrayList<JobType> checked_jobtypes = new ArrayList<>();
        for (ViewJobTypeSelection typeView : jobTypeList) {
            if (typeView.isSelected()) {
                checked_jobtypes.add(typeView.getJobType());
            }

        }

        bundle.putSerializable("jobtypes", checked_jobtypes);
        return bundle;
    }

    @Override
    public boolean validateData() {
        //TODO
        return true;
    }

    private class CallbackGetJobTypes extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {

                List<JobType> aux = new BuilderListJobType().build(response);

                for (JobType jobType : aux) {
                    jobTypeList.add(new ViewJobTypeSelection(jobType));
                }

                if (!isPopulated && recyclerView != null && getActivity() != null) {
                    populateRecyclerView();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListenerQueryTextJobType implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return onQueryTextSubmit(query);
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            if (adapter.hasNewFilter(newText)) {
                adapter.setFilter(newText);
                adapter.filterItems(100);
            }

            return true;
        }
    }
}