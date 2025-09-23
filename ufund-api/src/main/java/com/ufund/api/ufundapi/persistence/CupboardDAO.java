package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

public interface CupboardDAO {
        /**
     * Retrieves all {@linkplain Need needs}
     * 
     * @return An array of {@link Need need} objects, may be empty
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need[] getNeeds() throws IOException;

    /**
     * Searches for all {@linkplain Need needs} whose name contains the given text
     * 
     * @param containsText The text to match against
     * 
     * @return An array of {@link Need needs} whose names contains the given text, may be empty
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need[] searchNeeds(String containsText) throws IOException;

    /**
     * Retrieves a {@linkplain Need need} with the given id
     * 
     * @param id The id of the {@link Need need} to get
     * 
     * @return a {@link Need need} object with the matching id
     * <br>
     * null if no {@link Need need} with a matching id is found
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need getNeed(int id) throws IOException;

    /**
     * Creates and saves a {@linkplain Need need}
     * 
     * @param need {@linkplain Need need} object to be created and saved
     * <br>
     * The id of the need object is ignored and a new unique id is assigned
     *
     * @return new {@link Need need} if successful, false otherwise 
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need createNeed(Need need) throws IOException;

    /**
     * Updates and saves a {@linkplain Need need}
     * 
     * @param {@link Need need} object to be updated and saved
     * 
     * @return updated {@link Need need} if successful, null if
     * {@link Need need} could not be found
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    Need updateNeed(Need need) throws IOException;

    /**
     * Deletes a {@linkplain Need need} with the given id
     * 
     * @param id The id of the {@link Need need}
     * 
     * @return true if the {@link Need need} was deleted
     * <br>
     * false if need with the given id does not exist
     * 
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean deleteNeed(int id) throws IOException;
}
