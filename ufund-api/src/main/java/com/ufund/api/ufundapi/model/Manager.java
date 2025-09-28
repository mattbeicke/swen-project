package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Manager {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public Manager(String username, String password){
        this.username = "Admin";
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
