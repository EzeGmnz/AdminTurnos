package com.adminturnos.Activities.NewPlace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.R;
import com.google.android.material.textfield.TextInputEditText;

public class NewPlaceAFragment extends ObjectConfigurator {

    TextInputEditText etName, etAddress, etPhone, etCity, etState, etCountry;

    public NewPlaceAFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_place_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.editTextName);
        etAddress = view.findViewById(R.id.editTextAddress);
        etPhone = view.findViewById(R.id.editTextPhone);
        etCity = view.findViewById(R.id.editTextCity);
        etState = view.findViewById(R.id.editTextState);
        etCountry = view.findViewById(R.id.editTextCountry);
    }

    @Override
    public void setExtras(Bundle bundle) {

    }

    @Override
    public Bundle getData() {
        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String phone = etPhone.getText().toString();
        String city = etCity.getText().toString();
        String state = etState.getText().toString();
        String country = etCountry.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("street", address);
        bundle.putString("streetnumber", "");
        bundle.putString("city", city);
        bundle.putString("state", state);
        bundle.putString("country", country);
        bundle.putString("businessname", name);
        bundle.putString("phonenumber", phone);

        return bundle;
    }

    @Override
    public boolean validateData() {
        //TODO
        return true;
    }

}