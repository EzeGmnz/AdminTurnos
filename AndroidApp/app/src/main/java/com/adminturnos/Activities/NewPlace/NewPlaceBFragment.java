package com.adminturnos.Activities.NewPlace;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.ObjectViews.ViewAddress;
import com.adminturnos.R;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class NewPlaceBFragment extends ObjectConfigurator {

    private SearchView searchViewAddress;
    private FlexibleAdapter<ViewAddress> adapter;

    public NewPlaceBFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_place_b, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewAddress = view.findViewById(R.id.searchViewAddress);
        RecyclerView recyclerViewAddress = view.findViewById(R.id.recyclerViewAddress);

        searchViewAddress.setOnQueryTextListener(new ListenerQueryAddress());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAddress.setLayoutManager(layoutManager);
        adapter = new FlexibleAdapter<ViewAddress>(null);
        recyclerViewAddress.setAdapter(adapter);
        adapter.addListener(new OnAddressClickListener());
    }

    @Override
    public void setExtras(Bundle bundle) {

    }

    @Override
    public Bundle getData() {

        String address = searchViewAddress.getQuery().toString();

        Bundle bundle = new Bundle();
        bundle.putString("address", address);

        return bundle;
    }

    private void getAutoCompleteForAddress(String address) {
        List<String> addressList = new ArrayList<>();
        addressList.add(address);
        populateAutoComplete(addressList);
    }

    private void populateAutoComplete(List<String> addressList) {
        List<ViewAddress> viewAddressList = new ArrayList<>();
        for (String a : addressList) {
            viewAddressList.add(new ViewAddress(a));
        }
        adapter.clear();
        adapter.addItems(0, viewAddressList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean validateData() {

        //TODO Show error
        return !TextUtils.isEmpty(searchViewAddress.getQuery());
    }

    private void selectAddress(String address) {
        searchViewAddress.setQuery(address, true);
        hideKeyboard();
    }

    private void hideKeyboard() {
        searchViewAddress.clearFocus();
        View view = (View) getView().getRootView();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class ListenerQueryAddress implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return onQueryTextChange(query);
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            getAutoCompleteForAddress(newText);
            return true;
        }
    }

    private class OnAddressClickListener implements FlexibleAdapter.OnItemClickListener {
        @Override
        public boolean onItemClick(View view, int position) {
            selectAddress(adapter.getItem(position).getAddress());
            return false;
        }
    }
}