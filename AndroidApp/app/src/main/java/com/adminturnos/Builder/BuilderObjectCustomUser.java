package com.adminturnos.Builder;

import com.adminturnos.ObjectInterfaces.CustomUser;
import com.adminturnos.Objects.CustomUserImpl;

import org.json.JSONException;
import org.json.JSONObject;

public class BuilderObjectCustomUser implements ObjectBuilder<CustomUser> {

    @Override
    public CustomUser build(JSONObject json) throws JSONException {

        CustomUser out;

        out = new CustomUserImpl(
                json.getString("id"),
                json.getString("given_name"),
                json.getString("family_name"),
                json.getString("email")
        );

        return out;
    }
}
