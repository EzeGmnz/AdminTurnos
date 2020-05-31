package com.adminturnos;

public interface Values {

    /* USER MANAGEMENT */
    int RC_SIGN_UP = 1;

    /* POSTGRESQL */
    String POSTGRESQL_NAME = "ezegi";
    String POSTGRESQL_PASSWORD = "eolche28";
    String POSTGRESQL_DATABASE_NAME = "AdminTurnos";

    /* Google Console */
    String CLIENT_ID_ANDROID = "237762704075-aho2g7qkm3dftdnruib973oci987400o.apps.googleusercontent.com";
    String CLIENT_ID_WEB_APP = "237762704075-5l2qu43226aik1njlue1jhd9jh7r9d2c.apps.googleusercontent.com";
    String CLIENT_SECRET_WEB_APP = "JHZcY30PaCbHK36oUsmsFI8E";

    /* Django rest app */
    String CLIENT_ID_REST_APP = "rvZS5EmcLV10WJ4YWsbwMo1bH8N6WzkncAWFtBHN";
    String CLIENT_SECRET_REST_APP = "jgFSX4SN9PGq2CK2L71KkVsJstMVYJgQGMzEnXuwyOdyxL5Q5C72U8D5rtHSy5ZlLC3irsORHdwTVIH5UK9ve3lExaJJAqeQ3PsQWvwJBFVuPw1eXUOauhuaC7ioLuum";

    /* DJango URL */
    String DJANGO_URL_CONVERT_TOKEN = "http://192.168.1.34:8000/auth/login-android-google/";
}
