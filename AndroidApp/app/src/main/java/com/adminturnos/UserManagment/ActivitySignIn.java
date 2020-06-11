package com.adminturnos.UserManagment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.adminturnos.R;

public class ActivitySignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FragmentUserManagement fragmentUserManagement = new FragmentUserManagement();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_sign_in, fragmentUserManagement);
        transaction.commit();

    }
}