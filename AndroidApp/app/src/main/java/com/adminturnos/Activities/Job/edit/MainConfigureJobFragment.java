package com.adminturnos.Activities.Job.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.R;
import com.adminturnos.Values;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainConfigureJobFragment extends Fragment {

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
    private RecyclerView recyclerViewDays;

    public MainConfigureJobFragment(Job job) {
        this.job = job.clone();
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

        view.findViewById(R.id.btnServiceConfiguration).setOnClickListener(new ListenerBtnServiceConfiguration());
        initUI();
    }

    private void initUI() {
        LinearLayoutManager layoutManagerDays = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDays.setLayoutManager(layoutManagerDays);
        recyclerViewDays.setAdapter(new AdapterRecyclerViewDays());

    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
        initUI();
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

                holder.servicesContainer.removeAllViews();
                for (Provides p : daySchedule.getProvides()) {
                    View serviceView = createViewForService(p);
                    holder.servicesContainer.addView(serviceView);
                }

            } else {
                holder.labelClosed.setVisibility(View.VISIBLE);
                holder.servicesContainer.setVisibility(View.GONE);
                holder.labelStart.setVisibility(View.GONE);
                holder.labelEnd.setVisibility(View.GONE);
            }
        }

        private View createViewForService(Provides p) {
            TextView serviceView = new TextView(getContext());
            serviceView.setTextColor(getResources().getColor(R.color.white));
            serviceView.setGravity(Gravity.CENTER_HORIZONTAL);
            serviceView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            serviceView.setText(p.getService().getName());
            return serviceView;
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

    private class ListenerBtnServiceConfiguration implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ServiceConfigActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("job", job.clone());
            intent.putExtras(bundle);
            getActivity().startActivityForResult(intent, Values.RC_EDIT_SERVICES);
        }
    }
}