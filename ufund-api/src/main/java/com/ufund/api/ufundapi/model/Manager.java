package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Manager {
    @JsonProperty("password")
    private String password;

    public String getPassword() {
        return password;
    }
}
