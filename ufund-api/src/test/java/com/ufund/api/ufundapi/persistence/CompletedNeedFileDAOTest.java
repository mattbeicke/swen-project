package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.model.CompletedNeed;
import java.io.File;

@Tag("Persistence-tier")
public class CompletedNeedFileDAOTest {
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
    public void setupCompletedNeedFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testCN = new CompletedNeed[3];
        testCN[0] = new CompletedNeed((new Need("First Example", 61, "Requires one thing to be correct")), 1, 1000);
        testCN[1] = new CompletedNeed((new Need("Second Example", 62, "Requires many things to be correct")), 2, 1001);
        testCN[2] = new CompletedNeed((new Need("Second Example, Continued", 63, "Requires everything to be correct")),
                3, 1002);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the hero array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"), CompletedNeed[].class))
                .thenReturn(testCN);
        completedNeedFileDAO = new CompletedNeedFileDAO("doesnt_matter.txt", mockObjectMapper);
    }

    @Test
    public void testGetRecentNeedsOutOfBounds() {
        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds(2, 100);

        assertEquals(new CompletedNeed[0].length, cn.length);
    }

    @Test
    public void testGetRecentNeeds1() {
        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds(2, 1);

        assertEquals(testCN.length - 1, cn.length);
        assertEquals(testCN[1], cn[0]);
        assertEquals(testCN[2], cn[1]);
    }

    @Test
    public void testGetRecentNeeds2() {
        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds(2, 0);

        assertEquals(testCN.length - 1, cn.length);
        assertEquals(testCN[0], cn[0]);
        assertEquals(testCN[1], cn[1]);
    }

    @Test
    public void testCompleteNeed() {
        Need need = new Need("ned", 1, "desc");
        User user = new User(0, "uname", "pword", "", "");

        assertDoesNotThrow(() -> completedNeedFileDAO.completeNeed(need, user), "Unexpected exception thrown");

        CompletedNeed[] cn = completedNeedFileDAO.getRecentNeeds();

        CompletedNeed expected = new CompletedNeed(need, 0, Instant.now().getEpochSecond());

        assertEquals(expected.getNeed(), cn[3].getNeed());
        assertEquals(expected.getContributorID(), cn[3].getContributorID());
        assertEquals(expected.getTimestamp(), cn[3].getTimestamp());
    }
}
