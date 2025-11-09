package com.ufund.api.ufundapi.persistence;

import com.ufund.api.ufundapi.model.CompletedNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;

import java.io.IOException;

/**
 * Interface for Completed Need Data Access Object
 */
public interface CompletedNeedDAO {

    /**
     * Retrieves up to the 'count' most recent {@link CompletedNeed completed
     * needs}, sorted by time completed,
     * starting at the #'offset' item
     *
     * @param count  The number of needs to request,
     * @param offset The number of needs to request,
     * @return An array of {@link CompletedNeed need} objects, may be empty
     * @throws IOException if an issue with underlying storage
     */
    CompletedNeed[] getRecentNeeds(int count, int offset) throws IOException;

    /**
     * Retrieves all {@link CompletedNeed completed needs}, sorted by time completed
     *
     * @return An array of {@link CompletedNeed need} objects, may be empty
     * @throws IOException if an issue with underlying storage
     */
    CompletedNeed[] getRecentNeeds() throws IOException;

    /**
     * Searches for all {@link CompletedNeed completed needs} whose name contains the given text
     * 
     * @param containsText The text to match against
     * 
     * @return An array of {@link CompletedNeed completed needs} whose need names contains the given text,
     *         may be empty
     * 
     * @throws IOException if an issue with underlying storage
     */
    CompletedNeed[] searchCompletedNeeds(String containsText) throws IOException;

    /**
     * Completes a {@linkplain Need Need} and saves its completion data.
     *
     * @param need A {@link Need need} that has been completed.
     * @param user The {@link User user} completing the need.
     * @throws IOException if an issue with underlying storage
     */
    void completeNeed(Need need, User user) throws IOException;

    /**
     * gets how many Needs were completed in the past day, week, month, and year
     * 
     * @return array of integers containing the correspodning values listed above
     * @throws IOException
     */
    int[] getNumbers() throws IOException;
}
