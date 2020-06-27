package com.adminturnos.Activities.Job;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.ObjectInterfaces.Appointment;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectInterfaces.ServiceInstance;
import com.adminturnos.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAppointmentActivity extends AppCompatActivity {

    private static final Map<Integer, String> mapNumberDay = new HashMap<Integer, String>() {{
        put(1, "Dom");
        put(2, "Lun");
        put(3, "Mar");
        put(4, "Mie");
        put(5, "Jue");
        put(6, "Vie");
        put(7, "Sab");
    }};
    private Appointment appointment;
    private Job job;
    private RecyclerView recyclerViewServiceInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        getSupportActionBar().setTitle(appointment.getClient().getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        this.appointment = (Appointment) getIntent().getExtras().getSerializable("appointment");
        this.job = (Job) getIntent().getExtras().getSerializable("job");
        this.recyclerViewServiceInstance = findViewById(R.id.recyclerViewServiceInstance);
        initUI();
    }

    private void initUI() {
        TextView textViewDayIndicator = findViewById(R.id.textViewDayIndicator);
        TextView textViewDayNumberIndicator = findViewById(R.id.textViewDayNumberIndicator);
        TextView labelStartTime = findViewById(R.id.labelStartTime);
        TextView labelEndTime = findViewById(R.id.labelEndTime);

        ServiceInstance start = appointment.getServiceInstances().get(0);
        ServiceInstance end = appointment.getServiceInstances().get(appointment.getServiceInstances().size() - 1);

        String strStart = String.format("%02d:%02d", start.getDateTime().get(Calendar.HOUR_OF_DAY), start.getDateTime().get(Calendar.MINUTE));

        Provides p = job.getDaySchedule(appointment.getDate().get(Calendar.DAY_OF_WEEK)).getProvidesForService(end.getService().getId());
        Calendar endTime = (Calendar) end.getDateTime().clone();
        endTime.add(Calendar.HOUR_OF_DAY, p.getDuration().get(Calendar.HOUR_OF_DAY));
        endTime.add(Calendar.MINUTE, p.getDuration().get(Calendar.MINUTE));

        String strEnd = String.format("%02d:%02d", endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE));

        labelStartTime.setText(strStart);
        labelEndTime.setText(strEnd);

        textViewDayIndicator.setText(mapNumberDay.get(appointment.getDate().get(Calendar.DAY_OF_WEEK)));
        textViewDayNumberIndicator.setText(appointment.getDate().get(Calendar.DATE) + "/" + (1 + appointment.getDate().get(Calendar.MONTH)));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewServiceInstance.setLayoutManager(layoutManager);
        recyclerViewServiceInstance.setAdapter(new AdapterServiceInstance(appointment.getServiceInstances()));

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

    private class AdapterServiceInstance extends RecyclerView.Adapter<AdapterServiceInstance.ViewHolderServiceInstance> {

        List<ServiceInstance> serviceInstanceList;

        public AdapterServiceInstance(List<ServiceInstance> serviceInstanceList) {
            this.serviceInstanceList = serviceInstanceList;
        }

        @NonNull
        @Override
        public ViewHolderServiceInstance onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.appointment_view_service_layout, parent, false);

            return new ViewHolderServiceInstance(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderServiceInstance holder, int position) {
            ServiceInstance si = serviceInstanceList.get(position);

            Provides p = job.getDaySchedule(appointment.getDate().get(Calendar.DAY_OF_WEEK)).getProvidesForService(si.getService().getId());
            String strStart = String.format("%02d:%02d", si.getDateTime().get(Calendar.HOUR_OF_DAY), si.getDateTime().get(Calendar.MINUTE));
            String strDuration = "";
            if (p.getDuration().get(Calendar.HOUR_OF_DAY) > 0) {
                strDuration += String.format("%dh %02dm", p.getDuration().get(Calendar.HOUR_OF_DAY), p.getDuration().get(Calendar.MINUTE));
            } else {
                strDuration += String.format("%02dm", p.getDuration().get(Calendar.MINUTE));
            }

            holder.labelServiceInstanceStart.setText(strStart);
            holder.labelServiceInstanceName.setText(si.getService().getName());
            holder.labelServiceInstanceCost.setText("$" + p.getPrice());
            holder.labelServiceInstanceDuration.setText(strDuration);
        }

        @Override
        public int getItemCount() {
            return serviceInstanceList.size();
        }

        public class ViewHolderServiceInstance extends RecyclerView.ViewHolder {
            public TextView labelServiceInstanceStart;
            public TextView labelServiceInstanceName;
            public TextView labelServiceInstanceCost;
            public TextView labelServiceInstanceDuration;

            public ViewHolderServiceInstance(View view) {
                super(view);

                labelServiceInstanceStart = view.findViewById(R.id.labelServiceInstanceStart);
                labelServiceInstanceName = view.findViewById(R.id.labelServiceInstanceName);
                labelServiceInstanceCost = view.findViewById(R.id.labelServiceInstanceCost);
                labelServiceInstanceDuration = view.findViewById(R.id.labelServiceInstanceDuration);
            }
        }
    }
}