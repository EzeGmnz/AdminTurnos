package com.adminturnos.Activities.NewPlace;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Activities.ObjectConfiguratorCoordinator;
import com.adminturnos.Database.DatabaseDjangoWrite;
import com.adminturnos.R;
import com.adminturnos.Values;
import com.loopj.android.http.JsonHttpResponseHandler;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);

        initUI();
    }

    private void initUI() {
        setTitle("");

        List<ObjectConfigurator> fragments = new ArrayList<>();
        fragments.add(new NewPlaceAFragment());
        fragments.add(new NewPlaceBFragment());
        fragments.add(new NewPlaceCFragment());

        this.coordinator = new ObjectConfiguratorCoordinator(
                getSupportFragmentManager(),
                findViewById(android.R.id.content).getRootView(),
                fragments,
                new NewPlaceCoordinatorListener()
        );
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

        body.put("street", bundle.getString("street"));
        body.put("streetnumber", bundle.getString("streetnumber"));
        body.put("city", bundle.getString("city"));
        body.put("state", bundle.getString("state"));
        body.put("country", bundle.getString("country"));
        body.put("businessname", bundle.getString("businessname"));
        body.put("phonenumber", bundle.getString("phonenumber"));
        body.put("works_here", String.valueOf(bundle.getBoolean("works_here")));

        DatabaseDjangoWrite.getInstance().POST(
                Values.DJANGO_URL_NEW_PLACE,
                body,
                new CallbackNewPlace()
        );
    }

    private void createPlaceDoes(String id) {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<String> jobtypes = (List<String>) bundle.getSerializable("jobtypes");

        for (String type : jobtypes) {
            jsonArray.put(type);
        }

        try {
            json.put("jobtypes", jsonArray);
            json.put("place", id);

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

    private class CallbackNewPlace extends JsonHttpResponseHandler {

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                String id = response.getString("place_id");
                createPlaceDoes(id);

            } catch (JSONException ignored) {

            }

        }
    }

    private class CallbackPlaceDoes extends JsonHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            returnOK();
        }
    }
}