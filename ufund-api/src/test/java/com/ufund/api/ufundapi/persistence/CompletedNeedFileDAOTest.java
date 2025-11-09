package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.model.CompletedNeed;
import java.io.File;

@Tag("Persistence-tier")
class CompletedNeedFileDAOTest {
    CompletedNeedFileDAO completedNeedFileDAO;
    CompletedNeed[] testCN;
    ObjectMapper mockObjectMapper;

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * 
     * @throws IOException
     */
    @BeforeEach
    void setupCompletedNeedFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testCN = new CompletedNeed[5];
        testCN[0] = new CompletedNeed((new Need("First Example", 61, "Requires one thing to be correct")), 1, "alice",
                Instant.now().getEpochSecond());
        testCN[1] = new CompletedNeed((new Need("Second Example", 62, "Requires many things to be correct")), 2, "bob",
                Instant.now().minus(2, ChronoUnit.DAYS).getEpochSecond());
        testCN[2] = new CompletedNeed((new Need("Second Example, Continued", 63, "Requires everything to be correct")),
                3, "charles", Instant.now().minus(8, ChronoUnit.DAYS).getEpochSecond());
        testCN[3] = new CompletedNeed((new Need("Second Example, Continued", 63, "Requires everything to be correct")),
                4, "sad", Instant.now().minus(31, ChronoUnit.DAYS).getEpochSecond());
        testCN[4] = new CompletedNeed((new Need("Second Example, Continued", 63, "Requires everything to be correct")),
                5, "chuck", Instant.now().minus(366, ChronoUnit.DAYS).getEpochSecond());

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the hero array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"), CompletedNeed[].class))
                .thenReturn(testCN);
        completedNeedFileDAO = new CompletedNeedFileDAO("doesnt_matter.txt", mockObjectMapper);
    }

    @Test
    void testGetRecentNeedsOutOfBounds() {
        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds(2, 100);

        assertEquals(new CompletedNeed[0].length, cn.length);
    }

    @Test
    void testGetRecentNeeds1() {
        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds(2, 3);

        assertEquals(2, cn.length);
        assertEquals(testCN[3], cn[0]);
        assertEquals(testCN[4], cn[1]);
    }

    @Test
    void testGetRecentNeeds2() {
        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds(2, 0);

        assertEquals(2, cn.length);
        assertEquals(testCN[0], cn[0]);
        assertEquals(testCN[1], cn[1]);
    }

    @Test
    void testCompleteNeed() {
        Need need = new Need("ned", 1, "desc");
        User user = new User(0, "uname", "pword", "", "", 0, false);

        assertDoesNotThrow(() -> completedNeedFileDAO.completeNeed(need, user), "Unexpected exception thrown");

        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds();

        CompletedNeed expected = new CompletedNeed(need, 0, "id", Instant.now().getEpochSecond());

        assertEquals(expected.getNeed(), cn[0].getNeed());
        assertEquals(expected.getContributorID(), cn[0].getContributorID());
        assertEquals(expected.getTimestamp(), cn[0].getTimestamp());
    }

    @Test
    void testGetNums() {
        int[] nums = assertDoesNotThrow(() -> completedNeedFileDAO.getNumbers(), "Unexpected exception thrown");

        assertEquals(1, nums[0]);
        assertEquals(2, nums[1]);
        assertEquals(3, nums[2]);
        assertEquals(4, nums[3]);
    }

    @Test
    void testSearchCompletedNeeds() {
        String term = "tin";

        CompletedNeed[] cn = assertDoesNotThrow(() -> completedNeedFileDAO.searchCompletedNeeds(term),
                "Unexpected exception thrown");

        assertEquals(3, cn.length);
        assertEquals(testCN[2], cn[0]);
        assertEquals(testCN[3], cn[1]);
        assertEquals(testCN[4], cn[2]);
    }

    @Test
    void testSearchCompletedNeedsNull() {
        String term = null;

        CompletedNeed[] cn = assertDoesNotThrow(() -> completedNeedFileDAO.searchCompletedNeeds(term),
                "Unexpected exception thrown");

        assertEquals(testCN.length, cn.length);
        for (int i = 0; i < testCN.length; i++) {
            assertEquals(testCN[i], cn[i]);
        }
    }
}
