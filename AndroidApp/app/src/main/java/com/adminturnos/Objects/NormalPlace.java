package com.adminturnos.Objects;

import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.ObjectInterfaces.Place;

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
}
