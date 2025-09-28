package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.User;

@Component
public class UserFileDAO implements UserDAO {

    private static final int KEY_CHARACTERS = 32;

    Map<Integer, User> users; // Provides a local cache of the need objects
    // so that we don't need to read from the file
    // each time
    private ObjectMapper objectMapper; // Provides conversion between Need
    // objects and JSON text format written
    // to the file
    private static int nextId;
    private String filename; // Filename to read from and write to
    private Map<Integer, String> activeLogins;

    public UserFileDAO(@Value("${users.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        this.activeLogins = new HashMap<>();
        load();
    }

    private boolean load() throws IOException {
        users = new TreeMap<>();
        nextId = 0;

        // Deserializes the JSON objects from the file into an array of heroes
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        User[] userArray = objectMapper.readValue(new File(filename), User[].class);

        // Add each hero to the tree map and keep track of the greatest id
        for (User user : userArray) {
            users.put(user.getId(), user);
            if (user.getId() > nextId) {
                nextId = user.getId();
            }
        }
        // Make the next id one greater than the maximum from the file
        ++nextId;
        return true;
    }

    @Override
    public User addToBasket(User user, int needId) throws IOException {

        user.addToBasket(needId);

        return user;
    }

    @Override
    public User removeFromBasket(User user, int needId) throws IOException {

        user.removeFromBasket(needId);

        return user;
    }

    @Override
    public ArrayList<Integer> viewBasket(User user) throws IOException {

        return user.getBasket();
    }

    @Override
    public boolean checkout(User user) throws IOException {

        return user.checkout();

    }

    @Override
    public User getUser(int id) throws IOException {

        return users.get(id);

    }

    @Override
    public User getUserByUsername(String username) throws IOException {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User createUser(User user) throws IOException {
        if (getUserByUsername(user.getUsername()) != null)
            return null;
        User newUser = new User(user.getId(), user.getUsername(), user.getPassword());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(User user) throws IOException {
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser != user)
            return null;
        // only continues if the username is open (owned by the same user or not taken
        // at all)
        int id = user.getId();
        User oldUser = users.get(id);
        oldUser.updateUser(user.getUsername(), user.getPassword());
        users.put(id, oldUser);
        return oldUser;
    }

    @Override
    public boolean deleteUser(int id) throws IOException {
        return users.remove(id) != null;
    }

    @Override
    public boolean verifyLogin(String username, String password) throws IOException {
        if (getUserByUsername(username) == null)
            return false;
        return (getUserByUsername(username).getPassword().equals(password));
    }

    private String createLoginKey() {
        Random rand = new Random();
        String key = "";
        for (int i = 0; i < KEY_CHARACTERS; i++) {
            key = key + Integer.toHexString(rand.nextInt(16));
        }
        return key;
    }

    @Override
    public String attemptLogin(String username, String password) throws IOException {
        User user = getUserByUsername(username);
        if(user == null) return null;
        if(!verifyLogin(username, password)) return null;
        String new_key = createLoginKey();
        activeLogins.put(user.getId(), new_key);
        return new_key;
    }

    @Override
    public boolean verifyKey(String username, String key) throws IOException {
        User user = getUserByUsername(username);
        if(user == null) return false;
        if(!activeLogins.containsKey(user.getId())) return false;
        return activeLogins.get(user.getId()).equals(key);
    }

    @Override
    public void attemptLogout(String username) throws IOException {
        User user = getUserByUsername(username);
        if(user == null) return;
        activeLogins.remove(user.getId());
    }
}
