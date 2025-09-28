package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.ArrayList;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

public interface UserDAO {
    /**
     * Adds {@linkplain Need need} with given id to user's basket
     * 
     * @return Updated user object with added need
     * 
     * @throws IOException if an issue with underlying storage
     */
    User addToBasket(User user, int needId) throws IOException;

    /**
     * Removes {@linkplain Need need} with given id from a user's basket
     * 
     * @return Updated user object with removed need
     * 
     * @throws IOException if an issue with underlying storage
     */
    User removeFromBasket(User user, int needId) throws IOException;

    /**
     * Retrieves all {@linkplain Need needs} in that a user has
     * 
     * @return An array of {@link Need need} objects, may be empty
     * 
     * @throws IOException if an issue with underlying storage
     */
    ArrayList<Integer> viewBasket(User user) throws IOException;

    /**
     * "Checks out" all {@linkplain Need needs} in basket
     * 
     * @return true if sucessful, false if theres no items in checkout
     * 
     * @throws IOException if an issue with underlying storage
     */
    boolean checkout(User user) throws IOException;

    /**
     * Retrieves a {@linkplain User user} with the given username
     * 
     * @param username username of the user to find
     * 
     * @return the {@linkplain User user} with the username if it exists
     *         <br>
     *         null if no {@linkplain User user} with given username can be found
     * 
     * @throws IOException if an issue with underlying storage
     */
    User getUserByUsername(String username) throws IOException;

    /**
     * Retrieves a {@linkplain User user} with the given id
     * 
     * @param id The id of the {@link User user} to get
     * 
     * @return a {@link User user} object with the matching id
     *         <br>
     *         null if no {@link User user} with a matching id is found
     * 
     * @throws IOException if an issue with underlying storage
     */
    User getUser(int id) throws IOException;

    /**
     * Creates and saves a {@linkplain User user}
     * 
     * @param User {@linkplain User user} object to be created and saved
     *             <br>
     *             The id of the user object is ignored and a new unique id is
     *             assigned
     *
     * @return new {@link User user} if successful, false otherwise
     * 
     * @throws IOException if an issue with underlying storage
     */
    User createUser(User user) throws IOException;

    /**
     * Updates and saves a {@linkplain User user}
     * 
     * @param {@link User user} object to be updated and saved
     * 
     * @return updated {@link User user} if successful, null if
     *         {@link User user} could not be found
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    User updateUser(User user) throws IOException;

    /**
     * Deletes a {@linkplain User user} with the given id
     * 
     * @param id The id of the {@link User user}
     * 
     * @return true if the {@link User user} was deleted
     *         <br>
     *         false if user with the given id does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean deleteUser(int id) throws IOException;
}
