package com.adminturnos.Activities.Job.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Builder.BuilderListScheduleTemplate;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.ObjectInterfaces.DaySchedule;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.ObjectInterfaces.Provides;
import com.adminturnos.ObjectViews.ViewScheduleTemplate;
import com.adminturnos.Objects.DayScheduleNormal;
import com.adminturnos.Objects.NormalJob;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import eu.davidea.flexibleadapter.FlexibleAdapter;


public class SelectTemplateFragment extends ObjectConfigurator {

    private Job job;
    private RecyclerView recyclerViewTemplates;
    private FlexibleAdapter<ViewScheduleTemplate> adapter;
    private View.OnClickListener listenerInner;
    private ViewScheduleTemplate selectedTemplate;

    public SelectTemplateFragment(View.OnClickListener btnConfirmClickListener) {
        selectedTemplate = null;
        listenerInner = btnConfirmClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_select_template, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerViewTemplates = view.findViewById(R.id.recyclerViewTemplates);
        initUI();
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewTemplates.setLayoutManager(layoutManager);

        getScheduleTemplates();
    }

    private void populateScheduleTemplates(List<ViewScheduleTemplate> viewTemplateList) {
        adapter = new FlexibleAdapter<>(viewTemplateList);
        recyclerViewTemplates.setAdapter(adapter);
        adapter.addListener(new ListenerScheduleTemplateClick());
    }

    public void getScheduleTemplates() {
        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_SCHEDULE_TEMPLATES,
                null,
                new CallbackGetScheduleTemplates()
        );
    }

    private void onScheduleTemplateSelected(int position) {
        this.selectedTemplate = adapter.getItem(position);

    }

    private Job getPotentialJobWithSchedule(ViewScheduleTemplate selectedTemplate) {
        List<DaySchedule> dayScheduleList = new ArrayList<>();
        for (Integer i : selectedTemplate.getDays()) {
            dayScheduleList.add(new DayScheduleNormal(
                    null,
                    i,
                    selectedTemplate.getOpen(),
                    selectedTemplate.getClose(),
                    null,
                    null,
                    new ArrayList<Provides>()
            ));
        }
        return new NormalJob(job.getId(), job.getPlace(), dayScheduleList);
    }

    @Override
    public void setExtras(Bundle bundle) {
        this.job = (Job) bundle.getSerializable("job");
    }

    @Override
    public Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("job", getPotentialJobWithSchedule(selectedTemplate));
        return bundle;
    }

    @Override
    public boolean validateData() {
        return selectedTemplate != null;
    }

    private class ListenerScheduleTemplateClick implements FlexibleAdapter.OnItemClickListener {
        @Override
        public boolean onItemClick(View view, int position) {
            onScheduleTemplateSelected(position);
            listenerInner.onClick(view);
            return false;
        }
    }

    private class CallbackGetScheduleTemplates extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                populateScheduleTemplates(new BuilderListScheduleTemplate().build(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}