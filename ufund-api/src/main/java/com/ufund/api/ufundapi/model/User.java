package com.ufund.api.ufundapi.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("id")
    private int id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("basket")
    private ArrayList<Integer> basket;

    static final String STRING_FORMAT = "User [id=%d, username=%s, password=%s]";

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        basket = new ArrayList<>();
    }

    public void updateUser(String username, String password) {
        if (username != null) { // Only update what is new
            this.username = username;
        }
        if (password != null) {
            this.password = password;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getBasket() {
        return basket;
    }

    public void addToBasket(int needId) {
        basket.add(needId);
    }

    public void removeFromBasket(int needId) {
        basket.remove((Integer) needId);
    }

    public boolean inBasket(int needId) {
        return basket.contains((Integer) needId);
    }

    @Override
    public String toString() {
        return String.format(STRING_FORMAT, id, username, password);
    }
}
