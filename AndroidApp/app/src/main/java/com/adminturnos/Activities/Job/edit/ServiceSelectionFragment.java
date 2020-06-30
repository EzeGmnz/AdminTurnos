package com.adminturnos.Activities.Job.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Builder.BuilderListService;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Listeners.DatabaseCallback;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.ObjectViews.ViewService;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import eu.davidea.flexibleadapter.FlexibleAdapter;


public class ServiceSelectionFragment extends Fragment {

    private Job job;
    private FlexibleAdapter<ViewService> adapterServices;
    private RecyclerView recyclerViewServices;
    private SearchView searchViewDoableServices;
    private ServiceClickListener serviceClickListener;

    public ServiceSelectionFragment(Job job, ServiceClickListener listener) {
        this.job = job;
        this.serviceClickListener = listener;

        getServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.searchViewDoableServices = view.findViewById(R.id.searchViewDoableServices);
        this.recyclerViewServices = view.findViewById(R.id.recyclerViewDoableServices);
        RecyclerView.LayoutManager layoutManagerServices = new LinearLayoutManager(getContext());
        recyclerViewServices.setLayoutManager(layoutManagerServices);
    }

    private void getServices() {
        if (job != null) {
            Map<String, String> body = new HashMap<>();
            body.put("place_id", job.getPlace().getId());

            DatabaseDjangoRead.getInstance().GET(
                    Values.DJANGO_URL_DOABLE_SERVICES,
                    body,
                    new CallbackGetDoableServices()
            );
        }
    }

    private void populateServices(List<Service> serviceList) {

        List<ViewService> viewServiceList = new ArrayList<>();
        serviceList.sort(new ComparatorIsServiceProvided());
        for (Service s : serviceList) {
            viewServiceList.add(new ViewService(s, providesService(s)));
        }

        adapterServices = new FlexibleAdapter<>(viewServiceList);
        recyclerViewServices.setAdapter(adapterServices);

        this.searchViewDoableServices.setOnQueryTextListener(new ListenerQueryDoableServiceType());
        this.adapterServices.addListener(new RecyclerViewClickListener());
    }

    private boolean providesService(Service s) {
        for (DaySchedule ds : job.getDaySchedules()) {
            if (ds.getProvidesForService(s.getId()) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapterServices != null) {
            checkForUpdates();
        }
    }

    public void checkForUpdates() {
        for (ViewService vs : adapterServices.getCurrentItems()) {
            vs.setProvided(providesService(vs.getService()));
        }

        adapterServices.notifyDataSetChanged();
    }

    public interface ServiceClickListener {
        void onServiceViewClick(Service service);
    }

    private class CallbackGetDoableServices extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            populateServices(new BuilderListService().build(response));
        }
    }

    private class ListenerQueryDoableServiceType implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return onQueryTextSubmit(query);
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            if (adapterServices.hasNewFilter(newText)) {
                adapterServices.setFilter(newText);
                adapterServices.filterItems(100);
            }

            return true;
        }
    }

    private class RecyclerViewClickListener implements FlexibleAdapter.OnItemClickListener {

        @Override
        public boolean onItemClick(View view, int position) {
            serviceClickListener.onServiceViewClick(adapterServices.getItem(position).getService());
            return false;
        }

    }

    private class ComparatorIsServiceProvided implements java.util.Comparator<Service> {

        @Override
        public int compare(Service s1, Service s2) {
            boolean providesServiceS1 = providesService(s1);
            boolean providesServiceS2 = providesService(s2);

            if (providesServiceS1 && !providesServiceS2) {
                return -1;
            }
            if (providesServiceS2 && !providesServiceS1) {
                return 1;
            }

            return 0;
        }
    }
}