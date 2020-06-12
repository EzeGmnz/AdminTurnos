package com.adminturnos.Database;

import com.adminturnos.Values;

public class DatabaseDjango implements Database{

    @Override
    public String getUrl() {
        return Values.DJANGO_URL_BASE;
    }
}
