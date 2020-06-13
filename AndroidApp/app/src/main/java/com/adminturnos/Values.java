package com.adminturnos;

public interface Values {

    /* USER MANAGEMENT */
    int RC_SIGN_UP = 1;
    int RC_SIGN_IN_ACTIVITY = 2;

    /* Shared Preferences */
    String SHARED_PREF_NAME = "SharedPref";
    String SHARED_PREF_ACCESS_TOKEN = "AccessToken";

    /* Google Console */
    String CLIENT_ID_WEB_APP = "237762704075-uokqack5o2rdqd2ju15uprcc2ttkoo5j.apps.googleusercontent.com";

    /* Django URL */
    String DJANGO_URL_BASE = "http://192.168.1.50:8000";
    String DJANGO_URL_CONVERT_TOKEN = "/auth/login-android-google/";
    String DJANGO_URL_GET_JOBS = "/android/profile/jobs/";
    String DJANGO_URL_GET_OWNED_PLACES = "/android/profile/places/";
}
