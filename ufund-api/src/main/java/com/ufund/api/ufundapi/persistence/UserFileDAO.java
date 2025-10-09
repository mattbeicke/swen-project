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
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

/**
 * User Data Access Object, interacts with storage to get User objects
 * 
 * @author Zach Coy
 * @author Anthony Ficalora
 */
@Component
public class UserFileDAO implements UserDAO {

    private static final int KEY_CHARACTERS = 32;

    private Map<Integer, User> users; // Provides a local cache of the user objects
    // so that we don't need to read from the file each time
    private ObjectMapper objectMapper; // Provides conversion between User
    // objects and JSON text format written to the file
    private static int nextId;
    private String filename; // Filename to read from and write to
    private Map<Integer, String> activeLogins;

    public UserFileDAO(@Value("${users.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        this.activeLogins = new HashMap<>();
        load();
    }

    /**
     * Loads {@link User users} from the JSON file into the map
     * Also sets next id to one more than the greatest id found in the file
     *
     * @return true if the file was read successfully
     *
     * @throws IOException when file cannot be accessed or read from
     */
    private boolean load() throws IOException {
        users = new TreeMap<>();
        nextId = 0;

        // Deserializes the JSON objects from the file into an array of Users
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        User[] userArray = objectMapper.readValue(new File(filename), User[].class);

        // Add each user to the tree map and keep track of the greatest id
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

    /**
     * Saves the {@link User users} from the map into the file as an array
     * of JSON objects
     *
     * @return true if the {@link User users} were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        ArrayList<User> userArrayList = new ArrayList<>();
        for (User user : users.values()) {
            userArrayList.add(user);
        }
        User[] userArray = new User[userArrayList.size()];
        userArrayList.toArray(userArray);
        objectMapper.writeValue(new File(filename), userArray);
        return true;
    }

    /**
     * Generates the next id for a new {@link Need need}
     * 
     * @return The next id
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User addToBasket(User user, int needId) throws IOException {
        user.addToBasket(needId);
        return user;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User removeFromBasket(User user, int needId) throws IOException {
        user.removeFromBasket(needId);
        return user;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public ArrayList<Integer> viewBasket(User user) throws IOException {
        return user.getBasket();
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean checkout(User user) throws IOException {
        return user.checkout();
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User getUser(int id) throws IOException {
        return users.get(id);
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) throws IOException {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User createUser(User user) throws IOException {
        synchronized (users) {
            if(getUserByUsername(user.getUsername()) != null) {
                return null;
            }
            User newUser = new User(nextId(), user.getUsername(), user.getPassword());
            users.put(newUser.getId(), newUser);
            save(); // may throw an IOException
            return newUser;
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User updateUser(User user) throws IOException {
        synchronized (users) {
            if (users.containsKey(user.getId()) == false) {
                return null; // user does not exist
            }
            User prevUser = getUser(user.getId());
            prevUser.updateUser(user.getUsername(), user.getPassword());
            users.put(user.getId(), prevUser);
            save(); // may throw an IOException
            return prevUser;
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean deleteUser(int id) throws IOException {
        synchronized (users) {
            if (users.containsKey(id)) {
                users.remove(id);
                return save();
            } else {
                return false;
            }
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean verifyLogin(String username, String password) throws IOException {
        if (getUserByUsername(username) == null)
            return false;
        return (getUserByUsername(username).getPassword().equals(password));
    }

    /**
     ** {@inheritDoc}
     */
    private String createLoginKey() {
        Random rand = new Random();
        String key = "";
        for (int i = 0; i < KEY_CHARACTERS; i++) {
            key = key + Integer.toHexString(rand.nextInt(16));
        }
        return key;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public String attemptLogin(String username, String password) throws IOException {
        User user = getUserByUsername(username);
        if (user == null)
            return null;
        if (!verifyLogin(username, password))
            return null;
        String new_key = createLoginKey();
        activeLogins.put(user.getId(), new_key);
        return new_key;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean verifyKey(String username, String key) throws IOException {
        User user = getUserByUsername(username);
        if (user == null) return false;
        if (!activeLogins.containsKey(user.getId())) return false;
        return activeLogins.get(user.getId()).equals(key);
    }

    @Override
    public boolean verifyKey(int id, String key) throws IOException {
        User user = getUser(id);
        if(user == null) return false;
        if(!activeLogins.containsKey(user.getId())) return false;
        return activeLogins.get(user.getId()).equals(key);
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public void attemptLogout(String username) throws IOException {
        User user = getUserByUsername(username);
        if (user == null)
            return;
        activeLogins.remove(user.getId());
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean userIsManager(int id) throws IOException {
        return getUser(id).isManager();
    }
}
