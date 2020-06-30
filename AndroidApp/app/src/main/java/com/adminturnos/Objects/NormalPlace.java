package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.Place;

import java.util.Objects;

public class NormalPlace implements Place {
    private String id;
    private String businessName;
    private CustomUser owner;
    private String address;

    public NormalPlace(String id, CustomUser owner, String businessName, String address) {
        this.id = id;
        this.owner = owner;
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

    @Override
    public CustomUser getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalPlace that = (NormalPlace) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(businessName, that.businessName) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(address, that.address);
    }

}
