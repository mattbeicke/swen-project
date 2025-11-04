package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;
import java.io.File;

@Tag("Persistence-tier")
public class CupboardFileDAOTest {
    CupboardFileDAO cupboardFileDAO;
    Need[] testNeeds;
    ObjectMapper mockObjectMapper;

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * 
     * @throws IOException
     */
    @BeforeEach
    public void setupCupboardFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testNeeds = new Need[3];
        testNeeds[0] = new Need("First Example", 61, "Requires one thing to be correct");
        testNeeds[1] = new Need("Second Example", 62, "Requires many things to be correct");
        testNeeds[2] = new Need("Second Example, Continued", 63, "Requires everything to be correct");

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the need array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"), Need[].class))
                .thenReturn(testNeeds);
        cupboardFileDAO = new CupboardFileDAO("doesnt_matter.txt", mockObjectMapper);
    }

    @Test
    public void testGetAllNeeds() {
        Need[] needs = cupboardFileDAO.getNeeds();

        assertEquals(needs.length, testNeeds.length);
        for (int i = 0; i < testNeeds.length; ++i) {
            assertEquals(needs[i], testNeeds[i]);
        }
    }

    @Test
    public void testSearchNeeds() {
        Need[] needs = cupboardFileDAO.searchNeeds("Second"); // "Second .." and "Second .., Continued"

        assertEquals(needs.length, 2);
        assertEquals(needs[0], testNeeds[1]);
        assertEquals(needs[1], testNeeds[2]);
    }

    @Test
    public void testGetNeed() {
        Need need = cupboardFileDAO.getNeed(62);

        assertEquals(need, testNeeds[1]);
    }

    @Test
    public void testCreateNeed() {
        Need need = new Need("UFund", 5, "Testing Ex1");

        Need result = assertDoesNotThrow(() -> cupboardFileDAO.createNeed(need),
                "Unexpected exception thrown");

        assertNotNull(result);
        Need created = cupboardFileDAO.getNeed(result.getId());
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getName(), result.getName());
        assertEquals(created.getDescription(), result.getDescription());
    }

    @Test
    public void testUpdateNeed() {
        Need need = new Need("UFund", 5, "Testing Ex2");

        Need result = assertDoesNotThrow(() -> cupboardFileDAO.createNeed(need),
                "Unexpected exception thrown");

        assertNotNull(result);

        String newName = "Updated UFund";
        String newDescription = "Testing Ex3";

        Need updated = new Need(newName, result.getId(), newDescription);

        Need new_result = assertDoesNotThrow(() -> cupboardFileDAO.updateNeed(updated),
                "Unexpected exception thrown");

        assertNotNull(new_result);
        assertEquals(new_result.getName(), newName);
        assertEquals(new_result.getDescription(), newDescription);
    }

    @Test
    public void testUpdateNeedDoesNotExist() {
        Need need = new Need("UFund", 5, "Testing Ex2");

        // No need exists with id 5
        Need new_result = assertDoesNotThrow(() -> cupboardFileDAO.updateNeed(need),
                "Unexpected exception thrown");

        assertNull(new_result);
    }

    @Test
    public void testDeleteNeed() {
        Need need = new Need("UFund", 5, "Testing Ex1");

        Need result = assertDoesNotThrow(() -> cupboardFileDAO.createNeed(need),
                "Unexpected exception thrown");

        assertNotNull(result);
        Need created = cupboardFileDAO.getNeed(result.getId());

        int ID = created.getId();
        assertDoesNotThrow(() -> cupboardFileDAO.deleteNeed(ID),
                "Unexpected exception thrown");
        assertNull(cupboardFileDAO.getNeed(ID));
    }

    @Test
    public void testDeleteNeedDoesNotExist() {
        Need need = new Need("UFund", 5, "Testing Ex2");

        // No need exists with id 5
        boolean new_result = assertDoesNotThrow(() -> cupboardFileDAO.deleteNeed(need.getId()),
                "Unexpected exception thrown");

        assertFalse(new_result);
    }
}
