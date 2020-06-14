package com.adminturnos.Activities.NewPlace;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.Activities.ObjectConfiguratorCoordinator;
import com.adminturnos.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NewPlaceActivity extends AppCompatActivity {

    private PagerAdapter adapter;
    private ObjectConfiguratorCoordinator coordinator;

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
        super.onBackPressed();
        coordinator.prev();
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

    private class NewPlaceCoordinatorListener implements ObjectConfiguratorCoordinator.ListenerCoordinator {

        @Override
        public void onFinish(Bundle bundle) {
            Log.e("ASD", bundle.toString());
            returnOK();
        }

        @Override
        public void onCanceled() {
            returnCancel();
        }
    }

    private class CallbackNewPlace implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {

        }
    }
}