package com.adminturnos.Activities.Job.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Builder.BuilderListService;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.Service;
import com.adminturnos.ObjectViews.ViewService;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class MainConfigureJobFragment extends ObjectConfigurator {

    private static final Map<Integer, String> mapNumberDay = new HashMap<Integer, String>() {{
        put(1, "Dom");
        put(2, "Lun");
        put(3, "Mar");
        put(4, "Mie");
        put(5, "Jue");
        put(6, "Vie");
        put(7, "Sab");
    }};

    private Job job;
    private RecyclerView recyclerViewDays, recyclerViewServices;
    private boolean servicesPopulated;
    private List<ViewService> services;

    public MainConfigureJobFragment(Job job) {
        servicesPopulated = false;
        this.job = job;
        getServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_configure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.recyclerViewDays = view.findViewById(R.id.recyclerViewDays);
        this.recyclerViewServices = view.findViewById(R.id.recyclerViewServices);

        services = new ArrayList<>();

        if (job != null) {
            initUI();
        }
    }

    private void initUI() {
        LinearLayoutManager layoutManagerDays = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDays.setLayoutManager(layoutManagerDays);

        RecyclerView.LayoutManager layoutManagerServices = new LinearLayoutManager(getContext());
        recyclerViewServices.setLayoutManager(layoutManagerServices);

        AdapterRecyclerViewDays adapter = new AdapterRecyclerViewDays();
        recyclerViewDays.setAdapter(adapter);
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

    @Override
    public void setExtras(Bundle bundle) {
        job = (Job) bundle.getSerializable("job");
        if (recyclerViewDays != null) {
            initUI();
        }
    }

    @Override
    public Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("job", job);
        return bundle;
    }

    @Override
    public boolean validateData() {
        return true;
    }

    private void populateServices(JSONObject response) {
        servicesPopulated = true;
        List<Service> serviceList = new BuilderListService().build(response);
        for (Service s : serviceList) {
            services.add(new ViewService(s));
        }
        recyclerViewServices.setAdapter(new FlexibleAdapter<>(services));
    }

    private class AdapterRecyclerViewDays extends RecyclerView.Adapter<AdapterRecyclerViewDays.ViewHolderScheduleDays> {
        private int[] days;

        public AdapterRecyclerViewDays() {
            days = new int[]{1, 2, 3, 4, 5, 6, 7};
        }

        @Override
        public ViewHolderScheduleDays onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.day_job_edit_layout, parent, false);

            return new ViewHolderScheduleDays(v);
        }

        @Override
        public void onBindViewHolder(ViewHolderScheduleDays holder, int position) {
            holder.labelDay.setText(mapNumberDay.get(days[position]));
            if (hasScheduleThisDay(position)) {
                DaySchedule daySchedule = job.getDaySchedule(days[position]);
                holder.labelClosed.setVisibility(View.GONE);
                holder.servicesContainer.setVisibility(View.VISIBLE);
                holder.labelStart.setVisibility(View.VISIBLE);
                holder.labelEnd.setVisibility(View.VISIBLE);

                String startStr = String.format("%02d:%02d", daySchedule.getDayStart().get(Calendar.HOUR_OF_DAY), daySchedule.getDayStart().get(Calendar.MINUTE));
                String endStr = String.format("%02d:%02d", daySchedule.getDayEnd().get(Calendar.HOUR_OF_DAY), daySchedule.getDayEnd().get(Calendar.MINUTE));

                holder.labelStart.setText(startStr);
                holder.labelEnd.setText(endStr);

                for (Provides p : daySchedule.getProvides()) {
                    TextView serviceName = new TextView(getContext());
                    serviceName.setText(p.getService().getName());
                    holder.servicesContainer.addView(serviceName);
                }

            } else {
                holder.labelClosed.setVisibility(View.VISIBLE);
                holder.servicesContainer.setVisibility(View.GONE);
                holder.labelStart.setVisibility(View.GONE);
                holder.labelEnd.setVisibility(View.GONE);
            }
        }

        private boolean hasScheduleThisDay(int position) {
            return job.getDaySchedule(days[position]) != null;
        }

        @Override
        public int getItemCount() {
            return days.length;
        }

        public class ViewHolderScheduleDays extends RecyclerView.ViewHolder {
            public TextView labelDay, labelClosed, labelStart, labelEnd;
            public LinearLayout servicesContainer;

            public ViewHolderScheduleDays(View v) {
                super(v);
                this.labelDay = v.findViewById(R.id.labelDay);
                this.labelClosed = v.findViewById(R.id.labelClosed);
                this.servicesContainer = v.findViewById(R.id.recyclerViewServices);
                this.labelStart = v.findViewById(R.id.labelStart);
                this.labelEnd = v.findViewById(R.id.labelEnd);
            }
        }
    }

    private class CallbackGetDoableServices extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            populateServices(response);
        }
    }
}