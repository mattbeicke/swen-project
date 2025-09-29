package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class code for a {@link Manager manager} object
 * 
 * @author Ricardo Lopez
 */
public class Manager {
    @JsonProperty("username")
    private String username; // Manager's user name should always be admin
    @JsonProperty("password")
    private String password; // Manager's password

    /**
     * Manager constructor
     * 
     * @param username an ignored variable here
     * @param password Manager's password
     */
    public Manager(String username, String password) {
        this.username = "Admin";
        this.password = password;
    }

    /**
     * Returns the password
     * 
     * @return manager's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username
     * 
     * @return manager's username (should always be Admin)
     */
    public String getUsername() {
        return username;
    }
}
