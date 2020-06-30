package com.adminturnos.Activities.Place;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Builder.BuilderListPlace;
import com.adminturnos.Database.DatabaseDjangoRead;
import com.adminturnos.Listeners.DatabaseCallback;
import com.adminturnos.ObjectInterfaces.Place;
import com.adminturnos.R;
import com.adminturnos.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AdminPlacesActivity extends AppCompatActivity {
    private OnPlaceClickListener recyclerViewOwnedPlacesListener;
    private AdapterRecyclerViewOwnedPlaces adapterOwnedPlaces;
    private RecyclerView recyclerViewOwnedPlaces;
    private List<Place> ownedPlacesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_places);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setTitle("Administrar lugares");

        getOwnedPlaces();
    }

    private void initUI() {
        ownedPlacesList = new ArrayList<>();

        recyclerViewOwnedPlaces = findViewById(R.id.recyclerViewOwnedPlaces);
        recyclerViewOwnedPlacesListener = new OnPlaceClickListener();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewOwnedPlaces.setLayoutManager(layoutManager);

        adapterOwnedPlaces = new AdapterRecyclerViewOwnedPlaces(ownedPlacesList);
        recyclerViewOwnedPlaces.setAdapter(adapterOwnedPlaces);
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

    private void displayHasPlaces() {
        recyclerViewOwnedPlaces.setVisibility(View.VISIBLE);
    }

    private void displayNoPlaces() {
        recyclerViewOwnedPlaces.setVisibility(View.INVISIBLE);
    }

    private void getOwnedPlaces() {
        initUI();
        DatabaseDjangoRead.getInstance().GET(Values.DJANGO_URL_GET_OWNED_PLACES, null, new CallbackGetOwnedPlaces());
    }

    private void populatePlaces(JSONObject response) {
        try {
            ownedPlacesList.addAll(new BuilderListPlace().build(response));

            if (ownedPlacesList.size() > 0) {
                displayHasPlaces();
                adapterOwnedPlaces.notifyDataSetChanged();
            } else {
                displayNoPlaces();
            }

        } catch (JSONException ignored) {

        }
    }

    private class CallbackGetOwnedPlaces extends DatabaseCallback {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            populatePlaces(response);
        }
    }

    private class OnPlaceClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = recyclerViewOwnedPlaces.getChildLayoutPosition(view);
            Place place = ownedPlacesList.get(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("place", place);

            Intent intent = new Intent(getApplicationContext(), ViewPlaceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class AdapterRecyclerViewOwnedPlaces extends RecyclerView.Adapter<AdapterRecyclerViewOwnedPlaces.ViewHolderOwnedPlaces> {
        private List<Place> ownedPlaceList;

        public AdapterRecyclerViewOwnedPlaces(List<Place> ownedPlaces) {
            this.ownedPlaceList = ownedPlaces;
        }

        @Override
        public ViewHolderOwnedPlaces onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_place_item_layout, parent, false);

            v.setOnClickListener(recyclerViewOwnedPlacesListener);
            return new ViewHolderOwnedPlaces(v);
        }

        @Override
        public void onBindViewHolder(ViewHolderOwnedPlaces holder, int position) {
            holder.labelBusinessName.setText(ownedPlaceList.get(position).getBusinessName());
            holder.labelAddress.setText(ownedPlaceList.get(position).getAddress());
        }

        @Override
        public int getItemCount() {
            return ownedPlaceList.size();
        }

        public class ViewHolderOwnedPlaces extends RecyclerView.ViewHolder {
            public View relativeLayout;
            public TextView labelBusinessName;
            public TextView labelAddress;

            public ViewHolderOwnedPlaces(View v) {
                super(v);
                this.relativeLayout = v;
                this.labelBusinessName = v.findViewById(R.id.labelBusinessName);
                this.labelAddress = v.findViewById(R.id.labelAddress);
            }
        }
    }
}