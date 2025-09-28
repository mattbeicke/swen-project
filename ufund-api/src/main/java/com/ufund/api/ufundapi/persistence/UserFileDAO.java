package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.internal.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.User;

@Component
public class UserFileDAO implements UserDAO {

    private static final Logger LOG = Logger.getLogger(UserFileDAO.class.getName());
    Map<Integer, User> users; // Provides a local cache of the need objects
    // so that we don't need to read from the file
    // each time
    private ObjectMapper objectMapper; // Provides conversion between Need
    // objects and JSON text format written
    // to the file
    private static int nextId;
    private String filename; // Filename to read from and write to

    public UserFileDAO(@Value("${users.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
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
        User newUser = new User(user.getId(), user.getUsername(), user.getPassword());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(User user) throws IOException {
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
}
