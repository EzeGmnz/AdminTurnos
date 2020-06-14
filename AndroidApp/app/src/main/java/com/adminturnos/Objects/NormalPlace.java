package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.Place;

public class NormalPlace implements Place {
    private String id;
    private String businessName;
    private String address;

    public NormalPlace(String id, String businessName, String address) {
        this.id = id;
        this.businessName = businessName;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getAddress() {
        return address;
    }
}
