package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.ArrayList;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

/**
 * Interface for User Data Access Object
 * 
 * @author Zach Coy
 */
public interface UserDAO {
    /**
     * Adds {@link Need need} with given id to {@link User user's} basket
     * 
     * @param user   {@link User user} object whose basket is being added to
     * @param needId id of {@link Need need} to add
     * 
     * @return Updated {@link User user} object with added {@link Need need}
     * 
     * @throws IOException if an issue with underlying storage
     */
    User addToBasket(User user, int needId) throws IOException;

    /**
     * Removes {@link Need need} with given id from a {@link User user's} basket
     * 
     * @param user   {@link User user} object whose basket is being removed from
     * @param needId id of {@link Need need} to remove
     * 
     * @return Updated {@link User user} object with removed {@link Need need}
     * 
     * @throws IOException if an issue with underlying storage
     */
    User removeFromBasket(User user, int needId) throws IOException;

    /**
     * Retrieves all {@link Need needs} that a {@link User user} has in their basket
     * 
     * @param user {@link User user} object whose basket is being removed from
     * 
     * @return An array of {@link Need need} objects, may be empty
     * 
     * @throws IOException if an issue with underlying storage
     */
    ArrayList<Integer> viewBasket(User user) throws IOException;

    /**
     * "Checks out" all {@link Need needs} in basket
     * 
     * @param user {@link User user} object who is being checked out
     * 
     * @return true if sucessful, false if theres no items in the {@link User
     *         user's} basket
     * 
     * @throws IOException if an issue with underlying storage
     */
    boolean checkout(User user) throws IOException;

    /**
     * Tries to find a {@link User user} with the given username
     * 
     * @param username username of the user to find
     * 
     * @return the {@link User user} with the username if it exists or null if no
     *         {@link User user} with given username can be found
     * 
     * @throws IOException if an issue with underlying storage
     */
    User getUserByUsername(String username) throws IOException;

    /**
     * Retrieves a {@link User user} with the given id
     * 
     * @param id The id of the {@link User user} to find
     * 
     * @return a {@link User user} object with the matching id or null if no
     *         {@link User user} with a matching id is found
     * 
     * @throws IOException if an issue with underlying storage
     */
    User getUser(int id) throws IOException;

    /**
     * Creates and saves a {@link User user}
     * 
     * @param user {@link User User} object to be created and saved. The id of the
     *             user object is ignored and a new unique id is assigned
     *
     * @return a new {@link User user} if successful, null otherwise
     * 
     * @throws IOException if an issue with underlying storage
     */
    User createUser(User user) throws IOException;

    /**
     * Updates and saves a {@link User user} object
     * 
     * @param user a {@link User user} object to be updated and saved
     * 
     * @return updated {@link User user} if successful, null if
     *         {@link User user} could not be found
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    User updateUser(User user) throws IOException;

    /**
     * Deletes a {@link User user} with the given id
     * 
     * @param id The id of the {@link User user}
     * 
     * @return true if the {@link User user} was deleted, false if user with the
     *         given id does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean deleteUser(int id) throws IOException;

    /**
     * Checks if a entered username password combo exists
     * 
     * @param username username to check
     * @param password password to check
     * 
     * @return false if username does not exist or password is wrong, true otherwise
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean verifyLogin(String username, String password) throws IOException;

    /**
     * Creates a key, overwriting the previous one if necessary
     * 
     * @param username username to check
     * @param password password to check
     * 
     * @return created key or null if verifyLogin returns false
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    String attemptLogin(String username, String password) throws IOException;

    /**
     * Returns if a username key combo exist
     * 
     * @param username username to check
     * @param key      key to check
     * 
     * @return true if the key is the one currently assigned to that username
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean verifyKey(String username, String key) throws IOException;

    /**
     * Returns if an id key combo exist
     * 
     * @param id  id to check
     * @param key key to check
     * 
     * @return true if the key is the one currently assigned to that id
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean verifyKey(int id, String key) throws IOException;

    /**
     * Deletes the key for a user
     * 
     * @param username username of {@link User user} to whose key is to be deleted
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    void attemptLogout(String username) throws IOException;

    /**
     * Returns if the given user ID corresponds to a Manager
     * 
     * @param id id of the the {@link User user} to check
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    public boolean userIsManager(int id) throws IOException;
}
