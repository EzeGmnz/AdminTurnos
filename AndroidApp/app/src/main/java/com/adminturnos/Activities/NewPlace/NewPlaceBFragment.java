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

public class NewPlaceBFragment extends ObjectConfigurator {

    TextInputEditText etName, etAddress, etPhone, etCity, etState, etCountry;

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


    }

    @Override
    public Bundle getData() {
        Bundle bundle = new Bundle();

        return bundle;
    }

    @Override
    public boolean validateData() {
        //TODO
        return true;
    }

}