package com.adminturnos.MainScreen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adminturnos.Builder.BuilderJob;
import com.adminturnos.Builder.ObjectBuilder;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.ObjectInterfaces.Job;
import com.adminturnos.R;
import com.adminturnos.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
    private AdapterListViewJobs adapter;

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

        TextView name = view.findViewById(R.id.textViewName);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        name.setText(account.getDisplayName());
        getJobs();
    }

    private void getJobs() {
        populateJobs();
        DatabaseDjangoRead.getInstance().GET(Values.DJANGO_URL_GET_JOBS, null, new CallbackGetJobs());
    }

    private void populateJobs() {
        List<Job> jobList = new ArrayList<>();

        ListView listView = getView().findViewById(R.id.listViewJobs);

        adapter = new AdapterListViewJobs(getContext(), jobList);
        listView.setAdapter(adapter);
    }


    private class AdapterListViewJobs extends ArrayAdapter<Job> {

        public AdapterListViewJobs(@NonNull Context context, List<Job> jobList) {
            super(context, 0, jobList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listview_jobs_item, parent, false);
            }

            TextView textViewBusinessName = convertView.findViewById(R.id.textviewBusinessName);
            TextView textViewAddress = convertView.findViewById(R.id.textviewAddress);

            textViewBusinessName.setText(getItem(position).getPlace().getBusinessName());
            textViewAddress.setText(getItem(position).getPlace().getAddress());

            return convertView;
        }
    }

    private class CallbackGetJobs implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                final List<Job> jobList = new ArrayList<>();

                Log.e("ASDDS", jsonObject.toString());
                JSONArray jobs = (JSONArray) jsonObject.get("jobs");
                ObjectBuilder<Job> builder = new BuilderJob();

                for (int i = 0; i < jobs.length(); i++) {
                    jobList.add(builder.build(jobs.getJSONObject(i)));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(jobList);
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (JSONException e) {

            }
        }
    }
}