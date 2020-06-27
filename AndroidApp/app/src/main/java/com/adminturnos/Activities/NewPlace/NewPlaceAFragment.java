package com.adminturnos.Activities.NewPlace;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adminturnos.Activities.ObjectConfigurator;
import com.adminturnos.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class NewPlaceAFragment extends ObjectConfigurator {

    TextInputEditText etName;

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
    }

    @Override
    public void setExtras(Bundle bundle) {

    }

    @Override
    public Bundle getData() {
        String name = etName.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("businessname", name);
        return bundle;
    }

    @Override
    public boolean validateData() {

        TextInputLayout layoutName = getView().findViewById(R.id.text_input_layout_name);
        if (TextUtils.isEmpty(etName.getText())) {
            layoutName.setError("Este campo es obligatorio");
            return false;
        } else {
            layoutName.setError(null);
        }

        return true;
    }

}