package com.adminturnos.Activities.JobRequest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Builder.BuilderListPlace;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.ObjectViews.ViewPlace;
import com.adminturnos.R;
import com.adminturnos.Values;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class NewJobRequestActivity extends AppCompatActivity {

    private FlexibleAdapter<ViewPlace> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_request);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);

        initUI();
    }

    private void initUI() {
        setTitle("");
        SearchView searchView = findViewById(R.id.searchViewNewJobRequest);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNewJobRequest);

        searchView.setOnQueryTextListener(new QueryTextListenerNewJobRequest());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new FlexibleAdapter<>(null);
        adapter.mItemClickListener = new ItemClickListenerSearchPlace();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchPlace(String newText) {

        HashMap<String, String> body = new HashMap<>();
        body.put("searchquery", newText);

        DatabaseDjangoRead.getInstance().GET(
                Values.DJANGO_URL_SEARCH_PLACE,
                body,
                new CallbackGetSearchPlaces()
        );
    }

    private void showConfirmDialog(int position) {
        final ViewPlace vp = adapter.getItem(position);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar Solicitud de Trabajo")
                .setMessage(vp.getPlace().getBusinessName() + "\n" + vp.getPlace().getAddress())

                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendJobRequest(vp);
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    private void sendJobRequest(ViewPlace vp) {
        Map<String, String> body = new HashMap<>();
        body.put("place_id", vp.getPlace().getId());

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_NEW_JOB_REQUEST,
                body,
                new CallbackNewJobRequest()
        );
    }

    private static class CallbackNewJobRequest extends DatabaseCallback {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            //TODO
        }
    }

    private class QueryTextListenerNewJobRequest implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String newText) {
            searchPlace(newText);
            return true;
        }

        @Override
        public boolean onQueryTextChange(final String newText) {
            if (newText.equals("")) {
                adapter.clear();
                adapter.notifyDataSetChanged();
            }

            return true;
        }
    }

    private class CallbackGetSearchPlaces extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            try {
                List<Place> aux = new BuilderListPlace().build(response);

                adapter.clear();
                for (Place p : aux) {
                    adapter.addItem(new ViewPlace(p));
                }
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ItemClickListenerSearchPlace implements FlexibleAdapter.OnItemClickListener {
        @Override
        public boolean onItemClick(View view, int position) {
            showConfirmDialog(position);
            return true;
        }
    }
}