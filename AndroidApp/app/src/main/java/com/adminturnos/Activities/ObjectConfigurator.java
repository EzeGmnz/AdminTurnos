package com.adminturnos.Activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public abstract class ObjectConfigurator extends Fragment {

    public abstract void setExtras(Bundle bundle);

    public abstract Bundle getData();

    public abstract boolean validateData();

}
