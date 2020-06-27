package com.adminturnos.Activities.Place;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.R;

public class ViewPlaceActivity extends AppCompatActivity {

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        place = (Place) intent.getExtras().getSerializable("place");
        updateUI();
    }

    private void updateUI() {
        setTitle("");
        TextView textviewBusinessName = findViewById(R.id.textviewBusinessName);
        TextView textviewAddress = findViewById(R.id.textviewAddress);

        textviewBusinessName.setText(place.getBusinessName());
        textviewAddress.setText(place.getAddress());
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
}