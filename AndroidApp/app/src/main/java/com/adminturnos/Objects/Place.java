package com.adminturnos.Objects;

public class Place {
    private String id;
    private String businessName;
    private String address;

    public Place(String id, String businessName, String address) {
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
