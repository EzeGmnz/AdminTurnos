package com.adminturnos.Activities.NewPlace;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Activities.ObjectConfiguratorCoordinator;
import com.adminturnos.Database.DatabaseCallback;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.ObjectInterfaces.JobType;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class NewPlaceActivity extends AppCompatActivity {

    private ObjectConfiguratorCoordinator coordinator;
    private Bundle bundle;
    private List<ObjectConfigurator> fragments;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Registrar Lugar");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        fragments = new ArrayList<>();
        fragments.add(new NewPlaceAFragment());
        fragments.add(new NewPlaceBFragment());
        fragments.add(new NewPlaceCFragment());
        fragments.add(new NewPlaceDFragment());
        fragments.add(new NewPlaceEFragment());

        this.btnConfirm = findViewById(R.id.btn_confirm);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
    }

    private void initUI() {
        btnConfirm.setOnClickListener(new BtnConfirmClickListener());

        setTitle("");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                coordinator = new ObjectConfiguratorCoordinator(
                        getSupportFragmentManager(),
                        findViewById(android.R.id.content).getRootView(),
                        fragments,
                        new NewPlaceCoordinatorListener()
                );
                updateButton();
            }

        }, 100);

    }

    private void updateButton() {
        if (!coordinator.hasNext()) {
            btnConfirm.setText("Confirmar");
        } else {
            btnConfirm.setText("Siguiente");
        }
    }

    private void returnCancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void returnOK() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        coordinator.prev();
        updateButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToDatabase(Bundle bundle) {
        this.bundle = bundle;
        createNewPlace();
    }

    private void createNewPlace() {
        Map<String, String> body = new HashMap<>();

        body.put("address", bundle.getString("address"));
        body.put("businessname", bundle.getString("businessname"));
        body.put("phonenumber", bundle.getString("phonenumber"));
        body.put("works_here", String.valueOf(bundle.getBoolean("works_here")));

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_NEW_PLACE,
                body,
                new CallbackNewPlace()
        );
    }

    private void createPlaceDoes(String placeId) {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<JobType> jobtypes = (List<JobType>) bundle.getSerializable("jobtypes");

        for (JobType type : jobtypes) {
            jsonArray.put(type.getType());
        }

        try {
            json.put("jobtypes", jsonArray);
            json.put("place", placeId);

            DatabaseDjangoWrite.getInstance().POSTJSON(
                    this,
                    Values.DJANGO_URL_PLACE_DOES,
                    json,
                    new CallbackPlaceDoes()
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class NewPlaceCoordinatorListener implements ObjectConfiguratorCoordinator.ListenerCoordinator {

        @Override
        public void onFinish(Bundle bundle) {
            saveToDatabase(bundle);
        }

        @Override
        public void onCanceled() {
            returnCancel();
        }
    }

    private class CallbackNewPlace extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                String placeId = response.getString("place_id");
                createPlaceDoes(placeId);

            } catch (JSONException ignored) {

            }

        }
    }

    private class CallbackPlaceDoes extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            returnOK();
        }
    }

    private class BtnConfirmClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            coordinator.next();
            updateButton();
        }
    }
}